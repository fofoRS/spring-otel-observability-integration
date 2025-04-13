package org.personal.poc.events.observability.consumer;

import org.personal.poc.events.EnrichedUserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestClient;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class ConsumerConfig {

    @Value(value = "${external-endpoints.user-service-url}")
    private String userServiceUrl;

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
            String userServiceUriTemplate = String.format("%s/api/v1/users/{id}", userServiceUrl);

            log.info("full user-service url built: {}", userServiceUriTemplate);

            // http call to get user information from user-service application
            UserApiResponse userApiResponse = restClient.get()
            .uri(userServiceUriTemplate, event.userId())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                
                try {
                    String requestedUri = request.getURI().toString();
                    String responseBody = new String(response.getBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    log.error("User not found with id: {}, URL requested: {}, response returned {}", 
                        event.userId(), requestedUri, responseBody) ;
                } catch (IOException e) {
                    log.error("Error reading response body", e);
                }
                
            
            })
            .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                try {
                    String responseBody = new String(response.getBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    log.error("Server error occurred with status code {} and body {}", 
                        response.getStatusText(), 
                        responseBody);
                } catch (IOException e) {
                    log.error("Error reading response body", e);
                }
            })
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
