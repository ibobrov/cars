package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernateEngineRepository implements EngineRepository {
    private final Logger logger = LoggerFactory.getLogger(HibernateEngineRepository.class);
    private final CrudRepository crudRepo;

    @Override
    public Engine save(Engine engine) {
        try {
            crudRepo.run(session -> session.save(engine));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return engine;
    }

    @Override
    public boolean update(Engine engine) {
        var rsl = false;
        try {
            crudRepo.run(session -> session.update(engine));
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
            rsl = crudRepo.executeUpdate("DELETE FROM Engine WHERE id = :id", Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<Engine> findById(int id) {
        Optional<Engine> rsl = empty();
        try {
            rsl = crudRepo.optional("FROM Engine WHERE id = :id", Engine.class, Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Engine> getAll() {
        List<Engine> rsl = List.of();
        try {
            rsl = crudRepo.query("FROM Engine e ORDER BY e.name", Engine.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }
}
