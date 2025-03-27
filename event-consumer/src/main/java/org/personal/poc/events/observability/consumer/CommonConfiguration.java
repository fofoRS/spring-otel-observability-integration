package org.personal.poc.events.observability.consumer;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import io.micrometer.observation.ObservationRegistry;

@Configuration
public class CommonConfiguration {


    @Bean
    public RestClient restClient(ObservationRegistry observationRegistry) {
        return RestClient.builder().observationRegistry(observationRegistry).build();
    }
}
