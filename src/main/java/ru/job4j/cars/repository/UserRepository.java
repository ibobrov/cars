package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserRepository {
    private final SessionFactory sf;

    public User create(User user) {
        var session = sf.openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
        return user;
    }

    public void update(User user) {
        var session = sf.openSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(int userId) {
        var session = sf.openSession();
        session.beginTransaction();
        var user = new User();
        user.setId(userId);
        session.delete(user);
        session.getTransaction().commit();
        session.close();
    }

    public List<User> findAllOrderById() {
        var session = sf.openSession();
        session.beginTransaction();
        List<User> result = session.createQuery("FROM User ORDER BY id", User.class).list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public Optional<User> findById(int userId) {
        var session = sf.openSession();
        session.beginTransaction();
        var result = session.get(User.class, userId);
        session.getTransaction().commit();
        session.close();
        return Optional.ofNullable(result);
    }

    public List<User> findByLikeLogin(String key) {
        var session = sf.openSession();
        session.beginTransaction();
        var result = session.createQuery("FROM User WHERE login LIKE :key", User.class)
                            .setParameter("key", String.format("%%%s%%", key))
                            .list();
        session.getTransaction().commit();
        session.close();
        return result;
    }

    public Optional<User> findByLogin(String login) {
        var session = sf.openSession();
        session.beginTransaction();
        var result = session.createQuery("FROM User WHERE login = :login", User.class)
                            .setParameter("login", login)
                            .getSingleResult();
        session.getTransaction().commit();
        session.close();
        return Optional.ofNullable(result);
    }
}
