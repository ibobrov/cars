package ru.job4j.cars.repository;

import ru.job4j.cars.model.File;

import java.util.Optional;

public interface FileRepository {

    File save(File file);

    boolean delete(int id);

    Optional<File> findById(int id);
}
