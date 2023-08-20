package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernateOwnerRepository implements OwnerRepository {
    private final Logger logger = LoggerFactory.getLogger(HibernateOwnerRepository.class);
    private final CrudRepository crudRepo;

    @Override
    public Owner save(Owner owner) {
        try {
            crudRepo.run(session -> session.save(owner));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return owner;
    }

    @Override
    public boolean update(Owner owner) {
        var rsl = false;
        try {
            crudRepo.run(session -> session.update(owner));
            rsl = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public boolean delete(int id) {
        var rsl = false;
        try {
            rsl = crudRepo.executeUpdate("DELETE FROM Owner WHERE id = :id", Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<Owner> findById(int id) {
        Optional<Owner> rsl = empty();
        try {
            rsl = crudRepo.optional("FROM Owner o JOIN FETCH o.user WHERE o.id = :id", Owner.class, Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<Owner> findByUser(User user) {
        Optional<Owner> rsl = empty();
        try {
            rsl = crudRepo.optional("FROM Owner o JOIN FETCH o.user WHERE o.user.id = :id",
                    Owner.class, Map.of("id", user.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Owner> getAll() {
        List<Owner> rsl = List.of();
        try {
            rsl = crudRepo.query("FROM Owner o JOIN FETCH o.user", Owner.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }
}
