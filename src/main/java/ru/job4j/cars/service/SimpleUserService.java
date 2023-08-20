package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleUserService implements UserService {
    private final UserRepository userRepository;

    /**
     * For security purposes, the data from the request is validated against.
     * Do not think that the data from the http request will be correct.
     */
    @Override
    public Optional<User> save(User user) {
        if ("".equals(user.getLogin())
                || "".equals(user.getPassword())
                || userRepository.save(user).getId() == 0) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByLoginPassword(String login, String password) {
        return userRepository.findByLoginPassword(login, password);
    }
}
