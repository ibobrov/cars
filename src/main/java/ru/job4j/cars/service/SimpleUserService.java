package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.OwnerRepository;
import ru.job4j.cars.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleUserService implements UserService {
    private final UserRepository userRepo;
    private final OwnerRepository ownerRepo;
    private final Logger logger = LoggerFactory.getLogger(SimpleUserService.class);

    /**
     * For security purposes, the data from the request is validated against.
     * Do not think that the data from the http request will be correct.
     */
    @Override
    public Optional<User> save(User user, String fullName) {
        if ("".equals(user.getLogin())
                || "".equals(user.getPassword())
                || "".equals(fullName)
                || userRepo.save(user).getId() == 0
        ) {
            return Optional.empty();
        }
        if (ownerRepo.save(new Owner(0, fullName, user)).getId() == 0) {
            logger.error("Owner was not created from user: id = " + user.getId());
        }
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByLoginPassword(String login, String password) {
        return userRepo.findByLoginPassword(login, password);
    }
}
