package hanu.gdsc.infrastructure.controller;

import hanu.gdsc.domain.services.JudgeRunningSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControlController {
    @Autowired
    private JudgeRunningSubmissionService judgeRunningSubmissionService;

    @PostMapping("/start")
    public ResponseEntity<?> start() {
        judgeRunningSubmissionService.start();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stop() {
        judgeRunningSubmissionService.stop();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
