package hanu.gdsc.infrastructure.config;

import hanu.gdsc.domain.config.RunningSubmissionConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RunningSubmissionConfigImpl implements RunningSubmissionConfig {

    private static int MAX_THREAD = 2;
    private static int SCAN_RATE_MILLIS = 5000;
    private static int SCAN_LOCK_SECOND = 60 * 5;

    private static String VM_URL = "http://103.183.113.65:2358";
    private static String VM_TOKEN = "poopoopeepee";
    private static String VM_USER = "yowtf";

    public RunningSubmissionConfigImpl(Environment environment) {
        if (environment.getProperty("runningsubmission.maxthread") != null) {
            MAX_THREAD = Integer.parseInt(environment.getProperty("runningsubmission.maxthread"));
        }
        if (environment.getProperty("runningsubmission.scanratemillis") != null) {
            SCAN_RATE_MILLIS = Integer.parseInt(environment.getProperty("runningsubmission.scanratemillis"));
        }
        if (environment.getProperty("runningsubmission.scanlocksecond") != null) {
            SCAN_LOCK_SECOND = Integer.parseInt(environment.getProperty("runningsubmission.scanlocksecond"));
        }
        if (environment.getProperty("runningsubmission.vmurl") != null) {
            VM_URL = environment.getProperty("runningsubmission.vmurl");
        }
        if (environment.getProperty("runningsubmission.vmtoken") != null) {
            VM_TOKEN = environment.getProperty("runningsubmission.vmtoken");
        }
        if (environment.getProperty("runningsubmission.vmuser") != null) {
            VM_USER = environment.getProperty("runningsubmission.vmuser");
        }
    }

    @Override
    public int getMaxJudgingThread() {
        return MAX_THREAD;
    }

    @Override
    public int getScanRateMillis() {
        return SCAN_RATE_MILLIS;
    }

    @Override
    public int getScanLockSecond() {
        return SCAN_LOCK_SECOND;
    }

    @Override
    public String getVirtualMachineUrl() {
        return VM_URL;
    }

    @Override
    public String getVirtualMachineToken() {
        return VM_TOKEN;
    }

    @Override
    public String getVirtualMachineUser() {
        return VM_USER;
    }
}
