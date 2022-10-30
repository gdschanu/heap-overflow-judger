package hanu.gdsc.infrastructure.controller;

import com.sun.management.OperatingSystemMXBean;
import hanu.gdsc.domain.services.JudgeRunningSubmissionService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.List;

@RestController
public class StatusController {
    @Autowired
    JudgeRunningSubmissionService judgeRunningSubmissionService;

    @Builder
    public static class Output {
        public int cpuCores;
        public double cpuUsage;
        public long ram;
        public long ramUsage;
        public int maxJudgingThread;
        public int judgingThread;
        public List<String> virtualMachineUrls;
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Output output = Output.builder()
                .cpuCores(Runtime.getRuntime().availableProcessors())
                .cpuUsage(osBean.getProcessCpuLoad())
                .ram(Runtime.getRuntime().maxMemory())
                .ramUsage(Runtime.getRuntime().totalMemory())
                .maxJudgingThread(judgeRunningSubmissionService.maxJudgingThread())
                .judgingThread(judgeRunningSubmissionService.judgingThread())
                .virtualMachineUrls(judgeRunningSubmissionService.getVMUrls())
                .build();
        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
