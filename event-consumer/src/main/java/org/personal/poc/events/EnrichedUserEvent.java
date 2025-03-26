package org.personal.poc.events;

public record EnrichedUserEvent(String eventType, Long userId, Long timestamp, String userName, String country) {
    
}
