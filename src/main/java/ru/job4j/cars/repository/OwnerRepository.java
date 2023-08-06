package ru.job4j.cars.repository;

import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository {

    Owner save(Owner owner);

    boolean update(Owner owner);

    boolean delete(int id);

    Optional<Owner> findById(int id);

    List<Owner> getAll();
}
