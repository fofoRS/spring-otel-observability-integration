package org.personal.poc.events.observability.consumer;

import org.personal.poc.events.EnrichedUserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestClient;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class ConsumerConfig {

    Logger log = LoggerFactory.getLogger(ConsumerConfig.class);
    private final Tracer tracer;

    public ConsumerConfig(Tracer tracer, RestClient restClient) {
        this.tracer = tracer;
        this.restClient = restClient;
    }

    private final RestClient restClient;

    @Bean
    public Function<Message<UserEvent>, Message<UserEvent>> rawClickEvents() {
        return message -> {
            Span span = tracer.currentSpan();
            Baggage userIdBaggage = tracer.getBaggage("user.id");
            span.tag("user.id", userIdBaggage.get());
            log.info("Raw event received - {}", message.getPayload());
             return message;
        };
    }

    @Bean
    public Function<Message<UserEvent>, Message<EnrichedUserEvent>> enrichClickEvent() {
        return message -> {
            UserEvent event = message.getPayload();

            UserApiResponse userApiResponse = restClient.get()
            .uri("http://localhost:8080/api/v1/users/{id}", event.userId())
            .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> log.error("User not found with id: {}", event.userId()))
            .body(UserApiResponse.class);

            if (userApiResponse == null) {
                log.error("User not found with id: {}, cannot enrich event", event.userId());
                return null;
            } else {
                EnrichedUserEvent enrichedEvent = new EnrichedUserEvent(event.eventType(), event.userId(), event.timestamp(), userApiResponse.firstName(), userApiResponse.country());
                // Add enrichment logic here
                log.info("Event enriched - {}", enrichedEvent);
                return MessageBuilder.withPayload(enrichedEvent).build();
            }
        };
    }

    @Bean
    public Consumer<Message<EnrichedUserEvent>> logClickEvents() {
        return message -> {
            log.info("Final event logging - {}", message.getPayload());
        };
    }
}
