package org.personal.poc.events.observability.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class TimeBaseClickEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TimeBaseClickEventPublisher.class);

    private static final String outputChannel = "clientEvents-out-0";

    private final StreamBridge streamBridge;

    public TimeBaseClickEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
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

            UserEvent userEvent = new UserEvent(eventType,count,timestamp);

            logger.info("Publishing event - {}", userEvent);
            streamBridge.send(outputChannel,userEvent);

        }
    }

}
