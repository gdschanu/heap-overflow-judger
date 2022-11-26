package hanu.gdsc.infrastructure.repositories.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanu.gdsc.domain.models.Submission;
import hanu.gdsc.domain.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SubmissionRepositoryImpl implements SubmissionRepository {
    @Autowired
    private SubmissionJPARepository submissionJPARepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void save(Submission submission) {
        submissionJPARepository.save(SubmissionEntity.toEntity(submission, objectMapper));
    }
}
