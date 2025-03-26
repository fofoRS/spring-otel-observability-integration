package org.personal.poc.events.observability.user;

import lombok.Getter;

public class UserNotFoundException extends Exception {

    @Getter
    private Long requestedUser;

    public UserNotFoundException(Long requestedUser) {
        this.requestedUser = requestedUser;
    }


}
