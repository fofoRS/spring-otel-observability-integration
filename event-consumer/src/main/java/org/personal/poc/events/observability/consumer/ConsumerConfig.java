package org.personal.poc.events.observability.consumer;

import org.personal.poc.events.EnrichedUserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class ConsumerConfig {

    Logger log = LoggerFactory.getLogger(ConsumerConfig.class);

    @Bean
    public Function<Message<UserEvent>, Message<UserEvent>> rawClickEvents() {
        return message -> {
            log.info("Raw event received - {}", message.getPayload());
             return message;
        };
    }

    @Bean
    public Function<Message<UserEvent>, Message<EnrichedUserEvent>> enrichClickEvent() {
        return message -> {
            UserEvent event = message.getPayload();
            EnrichedUserEvent enrichedEvent = new EnrichedUserEvent(event.eventType(), event.userId(), event.timestamp(), "John Doe", "United States");
            // Add enrichment logic here
            log.info("Event enriched - {}", enrichedEvent);
            return MessageBuilder.withPayload(enrichedEvent).build();
        };
    }

    @Bean
    public Consumer<Message<EnrichedUserEvent>> logClickEvents() {
        return message -> {
            log.info("Final event logging - {}", message.getPayload());
        };
    }
}
