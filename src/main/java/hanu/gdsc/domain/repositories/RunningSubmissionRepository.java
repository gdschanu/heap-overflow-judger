package hanu.gdsc.domain.repositories;

import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.RunningSubmission;

import java.util.List;

public interface RunningSubmissionRepository {
    public void create(RunningSubmission runningSubmission);

    public RunningSubmission claim();

    public void delete(Id id);

    public void updateClaimed(RunningSubmission runningSubmission);

    public RunningSubmission getById(Id id, String serviceToCreate);

    public List<RunningSubmission> getByProblemIdAndCoderId(Id problemId,
                                                            Id coderId,
                                                            int page,
                                                            int perPage,
                                                            String serviceToCreate);
}
