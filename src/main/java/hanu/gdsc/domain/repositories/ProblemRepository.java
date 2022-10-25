package hanu.gdsc.domain.repositories;


import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.Problem;

import java.util.List;

public interface ProblemRepository {
    public Problem getById(Id id, String serviceToCreate);
}
