package hanu.gdsc.domain.repositories;

import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.SubmissionEvent;

public interface SubmissionEventRepository {
    public void save(SubmissionEvent submissionEvent);
}
