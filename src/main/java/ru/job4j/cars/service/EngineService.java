package ru.job4j.cars.service;

import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

public interface EngineService {

    Engine save(Engine engine);

    boolean update(Engine engine);

    boolean delete(int id);

    Optional<Engine> findById(int id);

    List<Engine> getAll();
}
