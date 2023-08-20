package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.EngineRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleEngineService implements EngineService {
    private EngineRepository engineRepo;

    @Override
    public Engine save(Engine engine) {
        return engineRepo.save(engine);
    }

    @Override
    public boolean update(Engine engine) {
        return engineRepo.update(engine);
    }

    @Override
    public boolean delete(int id) {
        return engineRepo.delete(id);
    }

    @Override
    public Optional<Engine> findById(int id) {
        return engineRepo.findById(id);
    }

    @Override
    public List<Engine> getAll() {
        return engineRepo.getAll();
    }
}
