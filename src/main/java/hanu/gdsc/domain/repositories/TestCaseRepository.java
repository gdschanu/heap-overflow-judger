package hanu.gdsc.domain.repositories;


import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.TestCase;

import java.util.List;

public interface TestCaseRepository {
    public List<TestCase> getByProblemId(Id problemId, String serviceToCreate);
}
