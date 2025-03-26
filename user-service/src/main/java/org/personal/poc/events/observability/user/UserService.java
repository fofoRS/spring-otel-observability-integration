package org.personal.poc.events.observability.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse findUser(Long userId) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        log.debug("User was found {}", userId);
        return new UserResponse(userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getAge(),
                userEntity.getCountry());
    }
}
