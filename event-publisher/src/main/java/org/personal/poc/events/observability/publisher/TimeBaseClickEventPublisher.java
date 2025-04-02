package org.personal.poc.events.observability.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

@Component
public class TimeBaseClickEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TimeBaseClickEventPublisher.class);

    private static final String outputChannel = "rawUserClickEvents-out-0";

    private final StreamBridge streamBridge;

    private final Tracer tracer;

    public TimeBaseClickEventPublisher(StreamBridge streamBridge, Tracer tracer) {
        this.streamBridge = streamBridge;
        this.tracer = tracer;
    }

    
    public void initPublishing() throws InterruptedException {
        String eventType = "userClickEvent";
        Long count = 0L;
        while(true) {
            if (count < 10) {
                count += 1L;
            } else {
                Thread.sleep(1000);
                count = 0L;
            }
            Long timestamp = System.currentTimeMillis();

            UserEvent userEvent = new UserEvent(eventType, count, timestamp);

            // Create a new span for each event
            Span span = tracer.nextSpan().name("raw-user-client-event-publisher");
            try (Tracer.SpanInScope ws = tracer.withSpan(span.start())) {
                logger.info("Publishing event - {}", userEvent);
                span.tag("event.type", userEvent.eventType());
                span.tag("timestamp", userEvent.timestamp().toString());    
                span.tag("user.id", userEvent.userId().toString());                

                try (var baggage = tracer.createBaggageInScope("user.id", userEvent.userId().toString())) {
                    streamBridge.send(outputChannel, userEvent);
                }
            } finally {
                span.end();
            }
        }
    }
}
