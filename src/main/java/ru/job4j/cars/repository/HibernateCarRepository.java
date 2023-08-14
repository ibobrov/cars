package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernateCarRepository implements CarRepository {
    private final Logger logger = LoggerFactory.getLogger(HibernateCarRepository.class);
    private final CrudRepository crudRepo;
    private final static String FIND_BY_ID = """
                                              SELECT DISTINCT c
                                              FROM Car c
                                              LEFT JOIN FETCH c.engine
                                              LEFT JOIN FETCH c.owner
                                              LEFT JOIN FETCH c.owners
                                              WHERE c.id = :id
                                              """;
    private final static String GET_ALL = """
                                              SELECT DISTINCT c
                                              FROM Car c
                                              LEFT JOIN FETCH c.engine
                                              LEFT JOIN FETCH c.owner
                                              LEFT JOIN FETCH c.owners
                                              """;

    @Override
    public Car save(Car car) {
        try {
            crudRepo.run(session -> session.save(car));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return car;
    }

    @Override
    public boolean update(Car car) {
        var rsl = false;
        try {
            crudRepo.run(session -> session.update(car));
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
            rsl = crudRepo.executeUpdate("DELETE FROM Car WHERE id = :id", Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<Car> findById(int id) {
        Optional<Car> rsl = empty();
        try {
            rsl = crudRepo.optional(FIND_BY_ID, Car.class, Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Car> getAll() {
        List<Car> rsl = List.of();
        try {
            rsl = crudRepo.query(GET_ALL, Car.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }
}
