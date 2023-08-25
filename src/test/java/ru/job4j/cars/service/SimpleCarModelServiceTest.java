package ru.job4j.cars.service;

import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.CarModel;
import ru.job4j.cars.repository.CarModelRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleCarModelServiceTest {
    private final CarModelRepository carModelRepo = mock(CarModelRepository.class);
    private final SimpleCarModelService carModelService = new SimpleCarModelService(carModelRepo);

    @Test
    void whenGetAllThenReturnCorrectResult() {
        var models = List.of(new CarModel(1, "", ""));
        assertThat(carModelRepo.getAll()).isEqualTo(List.of());
        when(carModelRepo.getAll()).thenReturn(models);
        assertThat(carModelService.getAll()).isEqualTo(models);
    }
}