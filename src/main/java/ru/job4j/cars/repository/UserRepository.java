package ru.job4j.cars.repository;

import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    boolean update(User user);

    boolean delete(int id);

    Optional<User> findById(int id);

    Optional<User> findByLoginPassword(String login, String password);

    List<User> getAll();
}
