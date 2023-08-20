package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.CarModel;
import ru.job4j.cars.repository.CarModelRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SimpleCarModelService implements CarModelService {
    private final CarModelRepository carModelRepo;

    @Override
    public List<CarModel> getAll() {
        return carModelRepo.getAll();
    }
}
