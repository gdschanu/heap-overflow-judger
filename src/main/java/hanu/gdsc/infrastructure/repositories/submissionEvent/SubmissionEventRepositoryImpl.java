package hanu.gdsc.infrastructure.repositories.submissionEvent;

import hanu.gdsc.domain.models.SubmissionEvent;
import hanu.gdsc.domain.repositories.SubmissionEventRepository;
import hanu.gdsc.domain.models.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class SubmissionEventRepositoryImpl implements SubmissionEventRepository {
    @Autowired
    private EventJPARepository eventJPARepository;

    @Override
    public void save(SubmissionEvent submissionEvent) {
        eventJPARepository.save(SubmissionEventEntity.toEntity(submissionEvent));
    }

    public void delete(Id id) {
        eventJPARepository.deleteById(id.toString());
    }
}
