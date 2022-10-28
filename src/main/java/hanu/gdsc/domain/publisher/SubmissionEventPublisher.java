package hanu.gdsc.domain.publisher;

import hanu.gdsc.domain.models.SubmissionEvent;

public interface SubmissionEventPublisher {
    public void publish(SubmissionEvent submissionEvent);
}
