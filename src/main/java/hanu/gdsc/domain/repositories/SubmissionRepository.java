package hanu.gdsc.domain.repositories;

import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.Submission;

import java.util.List;

public interface SubmissionRepository {
    public void save(Submission submission);
}
