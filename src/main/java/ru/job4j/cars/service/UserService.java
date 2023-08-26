package ru.job4j.cars.service;


import ru.job4j.cars.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> save(User user, String fullName);

    Optional<User> findByLoginPassword(String login, String password);
}
