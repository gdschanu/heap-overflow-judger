package hanu.gdsc.infrastructure.repositories.runningSubmission;

import hanu.gdsc.domain.config.RunningSubmissionConfig;
import hanu.gdsc.domain.models.Id;
import hanu.gdsc.domain.models.RunningSubmission;
import hanu.gdsc.domain.repositories.RunningSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RunningSubmissionRepositoryImpl implements RunningSubmissionRepository {
    @Autowired
    private RunningSubmissionJPARepository runningSubmissionJPARepository;
    @Autowired
    private RunningSubmissionConfig runningSubmissionConfig;

    private long getLockedUntil() {
        return System.currentTimeMillis() + runningSubmissionConfig.getScanLockSecond() * 1000;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public RunningSubmission claim(List<String> serviceToCreate) {
        RunningSubmissionEntity runningSubmission = runningSubmissionJPARepository.claim(
                System.currentTimeMillis(),
                serviceToCreate
        );
        if (runningSubmission == null) {
            return null;
        }
        runningSubmission.setLocked(1);
        runningSubmission.setLockedUntil(getLockedUntil());
        runningSubmissionJPARepository.save(runningSubmission);
        RunningSubmission domain = runningSubmission.toDomain();
        domain.increaseVersion();
        return domain;
    }

    @Override
    public void delete(Id id) {
        runningSubmissionJPARepository.deleteById(id.toString());
    }


    @Override
    public void updateClaimed(RunningSubmission runningSubmission) {
        RunningSubmissionEntity entity = RunningSubmissionEntity.fromDomain(runningSubmission, 1, getLockedUntil());
        runningSubmissionJPARepository.save(entity);
        runningSubmission.increaseVersion();
    }
}
