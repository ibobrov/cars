package ru.job4j.cars.service;

import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.EngineRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleEngineServiceTest {
    private final EngineRepository engineRepo = mock(EngineRepository.class);
    private final SimpleEngineService engineService = new SimpleEngineService(engineRepo);

    @Test
    void whenSaveThenReturnCorrectResult() {
        var engine = new Engine(1);
        when(engineRepo.save(engine)).thenReturn(engine);
        assertThat(engineService.save(engine)).isEqualTo(engine);
    }

    @Test
    void whenUpdateThenReturnCorrectResult() {
        var engine = new Engine(1);
        assertThat(engineService.update(engine)).isFalse();
        when(engineRepo.update(engine)).thenReturn(true);
        assertThat(engineService.update(engine)).isTrue();
    }

    @Test
    void whenDeleteThenReturnCorrectResult() {
        assertThat(engineService.delete(1)).isFalse();
        when(engineRepo.delete(1)).thenReturn(true);
        assertThat(engineService.delete(1)).isTrue();
    }

    @Test
    void whenFindByIdThenReturnCorrectResult() {
        var engine = Optional.of(new Engine(1));
        assertThat(engineService.findById(1)).isEqualTo(empty());
        when(engineRepo.findById(1)).thenReturn(engine);
        assertThat(engineService.findById(1)).isEqualTo(engine);
    }

    @Test
    void whenGetAllThenReturnCorrectResult() {
        var engines = List.of(new Engine(1));
        assertThat(engineService.getAll()).isEqualTo(List.of());
        when(engineRepo.getAll()).thenReturn(engines);
        assertThat(engineService.getAll()).isEqualTo(engines);
    }
}