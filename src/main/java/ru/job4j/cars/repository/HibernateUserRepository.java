package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernateUserRepository implements UserRepository {
    private CrudRepository crudRepo;

    @Override
    public User save(User user) {
        try {
            crudRepo.run(session -> session.save(user));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean update(User user) {
        var rsl = false;
        try {
            crudRepo.run(session -> session.update(user));
            rsl = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public boolean delete(int id) {
        var rsl = false;
        try {
            rsl = crudRepo.executeUpdate("DELETE FROM User WHERE id = :id",
                    Map.of("id", id)) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> rsl = empty();
        try {
            rsl = crudRepo.optional("FROM User WHERE id = :id", User.class,
                    Map.of("id", id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Optional<User> findByLoginPassword(String login, String password) {
        Optional<User> rsl = empty();
        try {
            rsl = crudRepo.optional(
                    "FROM User WHERE login = :login AND password = :password",
                    User.class, Map.of("login", login, "password", password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public List<User> getAll() {
        List<User> rsl = List.of();
        try {
            rsl = crudRepo.query("FROM User", User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }
}
