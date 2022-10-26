package hanu.gdsc.domain.services;

import hanu.gdsc.domain.config.RunningSubmissionConfig;
import hanu.gdsc.domain.models.*;
import hanu.gdsc.domain.repositories.*;
import hanu.gdsc.domain.vm.VirtualMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class JudgeRunningSubmissionService {
    private ThreadPoolExecutor executor;
    private final RunningSubmissionRepository runningSubmissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final VirtualMachine virtualMachine;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionEventRepository submissionEventRepository;
    private final RunningSubmissionConfig runningSubmissionConfig;
    private boolean temporaryStop = false;
    private final String CONTEST_SERVICE_TO_CREATE = "contest";
    private final String PRACTICE_PROBLEM_SERVICE_TO_CREATE = "PracticeProblemService";
    private final List<String> serviceToCreates = Arrays.asList(CONTEST_SERVICE_TO_CREATE,
            PRACTICE_PROBLEM_SERVICE_TO_CREATE);

    public JudgeRunningSubmissionService(RunningSubmissionRepository runningSubmissionRepository,
                                         TestCaseRepository testCaseRepository,
                                         VirtualMachine virtualMachine,
                                         ProblemRepository problemRepository,
                                         SubmissionRepository submissionRepository,
                                         SubmissionEventRepository submissionEventRepository,
                                         RunningSubmissionConfig runningSubmissionConfig) {
        this.runningSubmissionRepository = runningSubmissionRepository;
        this.testCaseRepository = testCaseRepository;
        this.virtualMachine = virtualMachine;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.submissionEventRepository = submissionEventRepository;
        this.runningSubmissionConfig = runningSubmissionConfig;

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(runningSubmissionConfig.getMaxJudgingThread());
        new Scheduler(runningSubmissionConfig.getScanRateMillis(), new Scheduler.Runner() {
            @Override
            protected void run() throws Throwable {
                process();
            }
        }).start();
    }

    public int judgingThread() {
        return executor.getActiveCount();
    }

    public int maxJudgingThread() {
        return executor.getPoolSize();
    }

    public void updateMaxJudgingThread(int maxJudgingThread) {
        temporaryStop = true;
        while (judgingThread() > 0) ;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxJudgingThread);
        temporaryStop = false;
    }

    public void enableJudgeContest() {
        serviceToCreates.add(CONTEST_SERVICE_TO_CREATE);
    }

    public void disableJudgeContest() {
        serviceToCreates.remove(CONTEST_SERVICE_TO_CREATE);
    }

    public void enableJudgePracticeProblem() {
        serviceToCreates.add(PRACTICE_PROBLEM_SERVICE_TO_CREATE);
    }

    public void disableJudgePracticeProblem() {
        serviceToCreates.add(PRACTICE_PROBLEM_SERVICE_TO_CREATE);
    }

    private boolean allThreadsAreActive() {
        return executor.getActiveCount() == runningSubmissionConfig.getMaxJudgingThread();
    }

    private synchronized void process() {
        if (temporaryStop)
            return;
        if (allThreadsAreActive())
            return;
        RunningSubmission runningSubmission = runningSubmissionRepository.claim(serviceToCreates);
        if (runningSubmission != null) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        judgeSubmission(runningSubmission);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void judgeSubmission(RunningSubmission runningSubmission) throws IOException, InterruptedException {
        List<TestCase> testCases = TestCase.sortByOrdinal(
                testCaseRepository.getByProblemId(
                        runningSubmission.getProblemId(),
                        runningSubmission.getServiceToCreate()
                )
        );
        Problem problem = problemRepository.getById(
                runningSubmission.getProblemId(),
                runningSubmission.getServiceToCreate()
        );
        Millisecond maxRunTime = new Millisecond(0L);
        KB maxMem = new KB(0);
        int start = Math.max(
                0,
                runningSubmission.getJudgingTestCase() - 1
        );
        for (int i = start; i < testCases.size(); i++) {
            runningSubmission.setJudgingTestCase(i + 1);
            runningSubmission.setTotalTestCases(testCases.size());
            runningSubmissionRepository.updateClaimed(runningSubmission);

            TestCase testCase = testCases.get(i);
            MemoryLimit memoryLimit = problem.getMemoryLimitByProgrammingLanguage(
                    runningSubmission.getProgrammingLanguage()
            );
            TimeLimit timeLimit = problem.getTimeLimitByProgrammingLanguage(
                    runningSubmission.getProgrammingLanguage()
            );
            VirtualMachine.RunResult runResult = virtualMachine.run(
                    runningSubmission.getCode(),
                    testCase.getInput(),
                    runningSubmission.getProgrammingLanguage()
            );
            maxMem = KB.max(maxMem, runResult.memory());
            maxRunTime = Millisecond.max(maxRunTime, runResult.runTime());
            Submission submission = null;
            if (memoryLimit == null) {
                submission = Submission.createWithId(
                        runningSubmission.getId(),
                        runningSubmission.getProblemId(),
                        runningSubmission.getProgrammingLanguage(),
                        maxRunTime,
                        maxMem,
                        runningSubmission.getCode(),
                        Status.STDE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        runningSubmission.getServiceToCreate(),
                        runningSubmission.getCoderId(),
                        "No memory limit."
                );
            } else if (timeLimit == null) {
                submission = Submission.createWithId(
                        runningSubmission.getId(),
                        runningSubmission.getProblemId(),
                        runningSubmission.getProgrammingLanguage(),
                        maxRunTime,
                        maxMem,
                        runningSubmission.getCode(),
                        Status.STDE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        runningSubmission.getServiceToCreate(),
                        runningSubmission.getCoderId(),
                        "No time limit."
                );
            } else if (runResult.compilationError()) {
                submission = Submission.createWithId(
                        runningSubmission.getId(),
                        runningSubmission.getProblemId(),
                        runningSubmission.getProgrammingLanguage(),
                        maxRunTime,
                        maxMem,
                        runningSubmission.getCode(),
                        Status.CE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        runningSubmission.getServiceToCreate(),
                        runningSubmission.getCoderId(),
                        runResult.compilationMessage()
                );
            } else if (runResult.stdError()) {
                submission = Submission.createWithId(
                        runningSubmission.getId(),
                        runningSubmission.getProblemId(),
                        runningSubmission.getProgrammingLanguage(),
                        maxRunTime,
                        maxMem,
                        runningSubmission.getCode(),
                        Status.STDE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        runningSubmission.getServiceToCreate(),
                        runningSubmission.getCoderId(),
                        runResult.stdMessage()
                );
            } else if (runResult.memory().greaterThan(memoryLimit.getMemoryLimit())) {
                submission = Submission.createWithId(
                        runningSubmission.getId(),
                        runningSubmission.getProblemId(),
                        runningSubmission.getProgrammingLanguage(),
                        maxRunTime,
                        maxMem,
                        runningSubmission.getCode(),
                        Status.MLE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                runResult.output().toString(),
                                testCase
                        ),
                        runningSubmission.getServiceToCreate(),
                        runningSubmission.getCoderId(),
                        runResult.stdMessage()
                );
            } else if (runResult.runTime().greaterThan(timeLimit.getTimeLimit())) {
                submission = Submission.createWithId(
                        runningSubmission.getId(),
                        runningSubmission.getProblemId(),
                        runningSubmission.getProgrammingLanguage(),
                        maxRunTime,
                        maxMem,
                        runningSubmission.getCode(),
                        Status.TLE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                runResult.output().toString(),
                                testCase
                        ),
                        runningSubmission.getServiceToCreate(),
                        runningSubmission.getCoderId(),
                        runResult.stdMessage()
                );
            } else {
                OutputComparator.CompareResult compareResult = OutputComparator.compare(
                        testCase.getExpectedOutput(),
                        runResult.output()
                );
                if (!compareResult.equal) {
                    submission = Submission.createWithId(
                            runningSubmission.getId(),
                            runningSubmission.getProblemId(),
                            runningSubmission.getProgrammingLanguage(),
                            maxRunTime,
                            maxMem,
                            runningSubmission.getCode(),
                            Status.WA,
                            FailedTestCaseDetail.fromTestCase(
                                    compareResult.differentLine,
                                    runResult.output().toString(),
                                    testCase
                            ),
                            runningSubmission.getServiceToCreate(),
                            runningSubmission.getCoderId(),
                            runResult.stdMessage()
                    );
                } else {
                    submission = Submission.createWithId(
                            runningSubmission.getId(),
                            runningSubmission.getProblemId(),
                            runningSubmission.getProgrammingLanguage(),
                            maxRunTime,
                            maxMem,
                            runningSubmission.getCode(),
                            Status.AC,
                            null,
                            runningSubmission.getServiceToCreate(),
                            runningSubmission.getCoderId(),
                            runResult.stdMessage()
                    );
                }
            }

            if (i == testCases.size() - 1 || !submission.getStatus().equals(Status.AC)) {
                saveSubmission(submission, runningSubmission);
                break;
            }
        }
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    private void saveSubmission(Submission submission, RunningSubmission runningSubmission) {
        runningSubmissionRepository.delete(runningSubmission.getId());
        submissionRepository.save(submission);
        submissionEventRepository.save(
                SubmissionEvent.create(
                        runningSubmission.getProblemId(),
                        submission.getStatus(),
                        submission.getCoderId()
                )
        );
    }
}
