package hanu.gdsc.infrastructure.publisher;

import hanu.gdsc.domain.models.SubmissionEvent;
import hanu.gdsc.domain.publisher.SubmissionEventPublisher;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionEventPublisherImpl implements SubmissionEventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    private static final String SUBMISSIONEVENTQUEUE = "Q_COREPROBLEM_SUBMISSIONEVENT";

    @Override
    public void publish(SubmissionEvent submissionEvent) {
        rabbitTemplate.convertAndSend(SUBMISSIONEVENTQUEUE, submissionEvent);
    }

    @Bean
    public Queue submissionEventQueue() {
        return new Queue(SUBMISSIONEVENTQUEUE, true);
    }
}
