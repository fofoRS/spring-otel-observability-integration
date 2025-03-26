package org.personal.poc.events.observability.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.tracing.Tracer;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Slf4j
public class ApiController {

    private final UserService userService;
    private final MeterRegistry meterRegistry;
    private final Tracer tracer;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id,
                                               @RequestParam(value = "fail", defaultValue = "false") boolean fail,
                                               @RequestParam(value = "delayMillis", defaultValue = "0") Long delayMillis) throws UserNotFoundException {
        var span = tracer.currentSpan();
        span.tag("user.id", id.toString());
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            if (fail) {
                log.error("Failed to get user with id: {}", id);
                span.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get user: " + id));
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get user: " + id);
                
            }

            if (delayMillis > 0) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get user");
                }
            }   
            UserResponse userResponse = userService.findUser(id);
            log.debug("User found with id: {}", id);
            return ResponseEntity.ok(userResponse);
        } finally {
            sample.stop(Timer.builder("user.get.duration")
                    .tag("user.id", id.toString())
                    .register(meterRegistry));
        }
    }
}
