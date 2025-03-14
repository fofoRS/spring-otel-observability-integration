package org.personal.poc.events.observability.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class ConsumerConfig {

    Logger log = LoggerFactory.getLogger(ConsumerConfig.class);

    @Bean
    public Consumer<Message<UserEvent>> clickEvents() {
        return message -> {
            log.info("Event consumed - {}", message.getPayload());
        };
    }
}
