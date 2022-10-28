package hanu.gdsc.infrastructure.publisher;

import hanu.gdsc.domain.models.SubmissionEvent;
import hanu.gdsc.domain.publisher.SubmissionEventPublisher;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionEventPublisherImpl implements SubmissionEventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    private static final String ROUTING_KEY = "COREPROBLEM_SUBMISSIONEVENT";
    @Override
    public void publish(SubmissionEvent submissionEvent) {
        rabbitTemplate.convertAndSend(ROUTING_KEY, submissionEvent);
    }
}
