package hanu.gdsc.infrastructure.repositories.problem;

import hanu.gdsc.domain.models.Problem;
import hanu.gdsc.domain.repositories.ProblemRepository;
import hanu.gdsc.domain.repositories.ProblemRepository;
import hanu.gdsc.domain.models.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProblemRepositoryImpl implements ProblemRepository {
    @Autowired
    private ProblemJPARepository problemJPARepository;

    @Override
    public Problem getById(Id id, String serviceToCreate) {
        try {
            ProblemEntity problemEntity = problemJPARepository
                    .findByIdAndServiceToCreate(id.toString(), serviceToCreate);
            return ProblemEntity.toDomain(problemEntity);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
