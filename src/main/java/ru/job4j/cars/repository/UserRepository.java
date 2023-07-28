package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@AllArgsConstructor
public class UserRepository {
    private final SessionFactory sf;

    public User create(User user) {
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return user;
    }

    public void update(User user) {
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }

    public void delete(int userId) {
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            var user = new User();
            user.setId(userId);
            session.delete(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }

    public List<User> findAllOrderById() {
        List<User> rsl = List.of();
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            rsl = session.createQuery("FROM User ORDER BY id", User.class).list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return rsl;
    }

    public Optional<User> findById(int userId) {
        Optional<User> rsl = empty();
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            rsl = Optional.ofNullable(session.get(User.class, userId));
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return rsl;
    }

    public List<User> findByLikeLogin(String key) {
        List<User> rsl = List.of();
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            rsl = session.createQuery("FROM User WHERE login LIKE :key", User.class)
                    .setParameter("key", String.format("%%%s%%", key))
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return rsl;
    }

    public Optional<User> findByLogin(String login) {
        Optional<User> rsl = empty();
        final var session = sf.openSession();
        try (session) {
            session.beginTransaction();
            rsl = session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return rsl;
    }
}
