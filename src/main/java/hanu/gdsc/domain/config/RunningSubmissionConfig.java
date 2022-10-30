package hanu.gdsc.domain.config;

import java.util.List;

public interface RunningSubmissionConfig {
    public int getMaxJudgingThread();

    public int getScanRateMillis();

    public int getScanLockSecond();

    public List<String> getVirtualMachineUrls();

    public String getVirtualMachineToken();

    public String getVirtualMachineUser();

    public boolean getVirtualMachineDeleteSubmission();
}
