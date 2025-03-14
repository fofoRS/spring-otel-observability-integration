package org.personal.poc.events.observability.consumer;

public record UserEvent(String eventType, Long userId, Long timestamp) {
}
