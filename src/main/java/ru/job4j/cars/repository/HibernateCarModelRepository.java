package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.CarModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernateCarModelRepository implements CarModelRepository {
    private final Logger logger = LoggerFactory.getLogger(HibernateEngineRepository.class);
    private final CrudRepository crudRepo;

    @Override
    public CarModel save(CarModel model) {
        try {
            crudRepo.run(session -> session.save(model));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return model;
    }

    @Override
    public boolean update(CarModel model) {
        var rsl = false;
        try {
            crudRepo.run(session -> session.update(model));
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
            rsl = crudRepo.executeUpdate("DELETE FROM CarModel WHERE id = :id", Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<CarModel> findById(int id) {
        Optional<CarModel> rsl = empty();
        try {
            rsl = crudRepo.optional("FROM CarModel WHERE id = :id", CarModel.class, Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<CarModel> getAll() {
        List<CarModel> rsl = List.of();
        try {
            rsl = crudRepo.query("FROM CarModel", CarModel.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }
}
