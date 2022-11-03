package hanu.gdsc.domain.services;

import hanu.gdsc.domain.config.RunningSubmissionConfig;
import hanu.gdsc.domain.models.*;
import hanu.gdsc.domain.publisher.SubmissionEventPublisher;
import hanu.gdsc.domain.repositories.ProblemRepository;
import hanu.gdsc.domain.repositories.RunningSubmissionRepository;
import hanu.gdsc.domain.repositories.SubmissionRepository;
import hanu.gdsc.domain.repositories.TestCaseRepository;
import hanu.gdsc.domain.vm.VirtualMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class JudgeRunningSubmissionService {
    private ThreadPoolExecutor executor;
    private final RunningSubmissionRepository runningSubmissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final VirtualMachine virtualMachine;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionEventPublisher submissionEventPublisher;
    private final RunningSubmissionConfig runningSubmissionConfig;
    private final AtomicBoolean stopJudge = new AtomicBoolean(false);
    private final String CONTEST_SERVICE_TO_CREATE = "ContestService";
    private final String PRACTICE_PROBLEM_SERVICE_TO_CREATE = "PracticeProblemService";
    private final List<String> serviceToCreates = Arrays.asList(CONTEST_SERVICE_TO_CREATE, PRACTICE_PROBLEM_SERVICE_TO_CREATE);

    public JudgeRunningSubmissionService(RunningSubmissionRepository runningSubmissionRepository,
                                         TestCaseRepository testCaseRepository,
                                         VirtualMachine virtualMachine,
                                         ProblemRepository problemRepository,
                                         SubmissionRepository submissionRepository,
                                         SubmissionEventPublisher submissionEventPublisher,
                                         RunningSubmissionConfig runningSubmissionConfig) {
        this.runningSubmissionRepository = runningSubmissionRepository;
        this.testCaseRepository = testCaseRepository;
        this.virtualMachine = virtualMachine;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.submissionEventPublisher = submissionEventPublisher;
        this.runningSubmissionConfig = runningSubmissionConfig;

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(runningSubmissionConfig.getMaxJudgingThread());
        new Scheduler(runningSubmissionConfig.getScanRateMillis(), new Scheduler.Runner() {
            @Override
            protected void run() throws Throwable {
                process();
            }
        }).start();
    }

    public List<String> getVMUrls() {
        return runningSubmissionConfig.getVirtualMachineUrls();
    }

    public int judgingThread() {
        return executor.getActiveCount();
    }

    public int maxJudgingThread() {
        return executor.getPoolSize();
    }

    public void updateMaxJudgingThread(int maxJudgingThread) {
        stop();
        while (judgingThread() > 0) ;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxJudgingThread);
        start();
    }

    public void stop() {
        stopJudge.set(true);
    }

    public void start() {
        stopJudge.set(false);
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
        if (stopJudge.get())
            return;
        if (allThreadsAreActive())
            return;
        final RunningSubmission runningSubmission = runningSubmissionRepository.claim(serviceToCreates);
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
        final List<TestCase> testCases = TestCase.sortByOrdinal(
                testCaseRepository.getByProblemId(
                        runningSubmission.getProblemId(),
                        runningSubmission.getServiceToCreate()
                )
        );
        final Problem problem = problemRepository.getById(
                runningSubmission.getProblemId(),
                runningSubmission.getServiceToCreate()
        );
        Millisecond maxRunTime = new Millisecond(0L);
        KB maxMem = new KB(0);
        final int start = Math.max(
                0,
                runningSubmission.getJudgingTestCase() - 1
        );
        for (int i = start; i < testCases.size(); i++) {
            runningSubmission.setJudgingTestCase(i + 1);
            runningSubmission.setTotalTestCases(testCases.size());
            runningSubmissionRepository.updateClaimed(runningSubmission);

            final TestCase testCase = testCases.get(i);
            final MemoryLimit memoryLimit = problem.getMemoryLimitByProgrammingLanguage(
                    runningSubmission.getProgrammingLanguage()
            );
            final TimeLimit timeLimit = problem.getTimeLimitByProgrammingLanguage(
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
                submission = Submission.fromRunningSubmission(
                        runningSubmission,
                        maxRunTime,
                        maxMem,
                        Status.STDE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        "No memory limit."
                );
            } else if (timeLimit == null) {
                submission = Submission.fromRunningSubmission(
                        runningSubmission,
                        maxRunTime,
                        maxMem,
                        Status.STDE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        "No time limit."
                );
            } else if (runResult.compilationError()) {
                submission = Submission.fromRunningSubmission(
                        runningSubmission,
                        maxRunTime,
                        maxMem,
                        Status.CE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        runResult.compilationMessage()
                );
            } else if (runResult.stdError()) {
                submission = Submission.fromRunningSubmission(
                        runningSubmission,
                        maxRunTime,
                        maxMem,
                        Status.STDE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                "",
                                testCase
                        ),
                        runResult.stdMessage()
                );
            } else if (runResult.memory().greaterThan(memoryLimit.getMemoryLimit())) {
                submission = Submission.fromRunningSubmission(
                        runningSubmission,
                        maxRunTime,
                        maxMem,
                        Status.MLE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                runResult.output().toString(),
                                testCase
                        ),
                        runResult.stdMessage()
                );
            } else if (runResult.runTime().greaterThan(timeLimit.getTimeLimit())) {
                submission = Submission.fromRunningSubmission(
                        runningSubmission,
                        maxRunTime,
                        maxMem,
                        Status.TLE,
                        FailedTestCaseDetail.fromTestCase(
                                0,
                                runResult.output().toString(),
                                testCase
                        ),
                        runResult.stdMessage()
                );
            } else {
                OutputComparator.CompareResult compareResult = OutputComparator.compare(
                        testCase.getExpectedOutput(),
                        runResult.output()
                );
                if (!compareResult.equal) {
                    submission = Submission.fromRunningSubmission(
                            runningSubmission,
                            maxRunTime,
                            maxMem,
                            Status.WA,
                            FailedTestCaseDetail.fromTestCase(
                                    compareResult.differentLine,
                                    runResult.output().toString(),
                                    testCase
                            ),
                            runResult.stdMessage()
                    );
                } else {
                    submission = Submission.fromRunningSubmission(
                            runningSubmission,
                            maxRunTime,
                            maxMem,
                            Status.AC,
                            null,
                            runResult.stdMessage()
                    );
                }
            }

            if (i == testCases.size() - 1 || !submission.getStatus().equals(Status.AC)) {
                saveSubmission(
                        submission,
                        runningSubmission,
                        submission.getStatus().equals(Status.AC) ? testCases.size() : i,
                        testCases.size()
                );
                break;
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    private void saveSubmission(Submission submission,
                                RunningSubmission runningSubmission,
                                int passedTestCasesCount,
                                int totalTestCasesCount) {
        runningSubmissionRepository.delete(runningSubmission.getId());
        submissionRepository.save(submission);
        submissionEventPublisher.publish(SubmissionEvent.create(
                runningSubmission.getProblemId(),
                submission.getStatus(),
                submission.getCoderId(),
                runningSubmission.getSubmittedAt(),
                passedTestCasesCount,
                totalTestCasesCount,
                runningSubmission.getServiceToCreate()
        ));
    }
}
