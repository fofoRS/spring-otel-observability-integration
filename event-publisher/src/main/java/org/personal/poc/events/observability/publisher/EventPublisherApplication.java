package org.personal.poc.events.observability.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventPublisherApplication implements ApplicationRunner {

	@Autowired
	private TimeBaseClickEventPublisher timeBaseClickEventPublisher;

	public static void main(String[] args) {
		SpringApplication.run(EventPublisherApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		timeBaseClickEventPublisher.initPublishing();
	}
}
