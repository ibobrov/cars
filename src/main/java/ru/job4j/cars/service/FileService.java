package ru.job4j.cars.service;


import ru.job4j.cars.dto.DtoFile;

import java.util.Optional;

public interface FileService {

    Optional<DtoFile> getFileById(int id);
}
