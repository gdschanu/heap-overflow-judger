package hanu.gdsc.domain.repositories;

import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.RunningSubmission;

import java.util.List;

public interface RunningSubmissionRepository {

    public RunningSubmission claim(List<String> serviceToCreate);

    public void delete(Id id);

    public void updateClaimed(RunningSubmission runningSubmission);

}
