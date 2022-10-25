package hanu.gdsc.infrastructure.repositories.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import hanu.gdsc.domain.models.Submission;
import hanu.gdsc.domain.repositories.SubmissionRepository;
import hanu.gdsc.domain.exceptions.InvalidInputException;
import hanu.gdsc.domain.models.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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
