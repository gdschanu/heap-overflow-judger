package hanu.gdsc.infrastructure.controller;

import hanu.gdsc.domain.services.JudgeRunningSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {
    @Autowired
    private JudgeRunningSubmissionService judgeRunningSubmissionService;
    private final String contestServiceToCreate = "ContestService";
    private final String practiceProblemServiceToCreate = "PracticeProblemService";


    public static class Input {
        public int maxJudgingThread;
    }

    @PostMapping("/updateMaxJudgingThread")
    public ResponseEntity<?> updateMaxJudgingThread(@RequestBody Input input) {
        judgeRunningSubmissionService.updateMaxJudgingThread(input.maxJudgingThread);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @PostMapping("/enableJudgeContest")
    public ResponseEntity<?> enableJudgeContest() {
        judgeRunningSubmissionService.enableJudgeContest();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/disableJudgeContest")
    public ResponseEntity<?> disableJudgeContest() {
        judgeRunningSubmissionService.disableJudgeContest();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/enableJudgePracticeProblem")
    public ResponseEntity<?> enableJudgePracticeProblem() {
        judgeRunningSubmissionService.enableJudgePracticeProblem();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/disableJudgePracticeProblem")
    public ResponseEntity<?> disableJudgePracticeProblem() {
        judgeRunningSubmissionService.disableJudgePracticeProblem();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
