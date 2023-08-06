package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

    Car save(Car car);

    boolean update(Car car);

    boolean delete(int id);

    Optional<Car> findById(int id);

    List<Car> getAll();
}
