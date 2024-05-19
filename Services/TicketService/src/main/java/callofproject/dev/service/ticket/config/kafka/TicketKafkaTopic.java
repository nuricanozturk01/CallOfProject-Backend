package callofproject.dev.service.ticket.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Nuri Can ÖZTÜRK
 * Represents the ticket kafka topic.
 * CopyRight(C) 2023 by Call Of Project Teams.
 */
@Component
public class TicketKafkaTopic
{
    @Value("${spring.kafka.notification-topic-name}")
    private String m_topicName;

    @Value("${spring.kafka.email-topic-name}")
    private String m_emailTopicName;

    /**
     * Provide the notification topic.
     */
    @Bean("notificationTopic")
    public NewTopic provideNotificationTopic()
    {
        return TopicBuilder.name(m_topicName).build();
    }

    /**
     * Provide the email topic.
     */
    @Bean("emailTopic")
    public NewTopic provideEmailTopic()
    {
        return TopicBuilder.name(m_emailTopicName).build();
    }
}