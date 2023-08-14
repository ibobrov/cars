package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.File;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernateFileRepository implements FileRepository {
    private final Logger logger = LoggerFactory.getLogger(HibernateFileRepository.class);
    private final CrudRepository crudRepo;

    @Override
    public File save(File file) {
        try {
            crudRepo.run(session -> session.save(file));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return file;
    }

    @Override
    public boolean delete(int id) {
        var rsl = false;
        try {
            rsl = crudRepo.executeUpdate("DELETE FROM File WHERE id = :id", Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<File> findById(int id) {
        Optional<File> rsl = empty();
        try {
            rsl = crudRepo.optional("FROM File WHERE id = :id", File.class, Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    public List<File> getAll() {
        List<File> rsl = List.of();
        try {
            rsl = crudRepo.query("FROM File", File.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }
}
