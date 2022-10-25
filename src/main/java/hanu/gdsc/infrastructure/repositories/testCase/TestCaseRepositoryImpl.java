package hanu.gdsc.infrastructure.repositories.testCase;

import hanu.gdsc.domain.models.TestCase;
import hanu.gdsc.domain.repositories.TestCaseRepository;
import hanu.gdsc.domain.models.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TestCaseRepositoryImpl implements TestCaseRepository {
    @Autowired
    private TestCaseJPARepository testCaseJpaRepository;
    
    @Override
    public List<TestCase> getByProblemId(Id problemId, String serviceToCreate) {
        List<TestCaseEntity> testCasesEntity = testCaseJpaRepository.getByProblemIdAndServiceToCreate(problemId.toString(), serviceToCreate);
        return testCasesEntity.stream()
                .map(t -> TestCaseEntity.toDomain(t))
                .collect(Collectors.toList());
    }
}
