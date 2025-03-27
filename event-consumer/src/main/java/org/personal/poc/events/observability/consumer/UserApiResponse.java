package org.personal.poc.events.observability.consumer;

public record UserApiResponse(String firstName, String lastName, int age, String country) {
}
