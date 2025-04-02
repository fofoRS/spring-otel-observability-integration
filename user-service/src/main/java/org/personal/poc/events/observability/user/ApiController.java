package org.personal.poc.events.observability.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import io.micrometer.tracing.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Slf4j
public class ApiController {

    private final UserService userService;
    private final Tracer tracer;
    private final MeterRegistry meterRegistry;
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        
        tracer.currentSpan().error(ex); // mark current span as error

        meterRegistry
        .timer("user.not.found").count();  // increment timer count wen user not found.
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id,
            @RequestParam(value = "fail", defaultValue = "false") boolean fail,
            @RequestParam(value = "delayMillis", defaultValue = "0") Long delayMillis) throws UserNotFoundException {
        if (fail) {
            log.error("Failed to get user with id: {}", id);
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
    }
}
