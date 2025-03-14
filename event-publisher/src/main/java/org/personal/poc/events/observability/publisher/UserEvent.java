package org.personal.poc.events.observability.publisher;

public record UserEvent(String eventType, Long userId, Long timestamp) {
}
