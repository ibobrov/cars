package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.File;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateFileRepositoryTest {
    private static SessionFactory sf;
    private static HibernateFileRepository fileRepo;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        fileRepo = new HibernateFileRepository(crudRepo);
    }

    @AfterEach
    public void clearRepo() {
        for (var engine : fileRepo.getAll()) {
            fileRepo.delete(engine.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        sf.close();
    }

    @Test
    public void whenSaveEngineThenFindByIdSame() {
        var file = new File(-1, "name", "path");
        fileRepo.save(file);
        assertThat(fileRepo.findById(file.getId()).get())
                .usingRecursiveComparison().isEqualTo(file);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(fileRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteEngineThenDeleteReturnTrueAndFindByIdEmpty() {
        var file = new File(-1, "name", "path");
        fileRepo.save(file);
        var isDelete = fileRepo.delete(file.getId());
        assertThat(isDelete).isTrue();
        assertThat(fileRepo.findById(file.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistEngineThenDeleteReturnFalse() {
        var isDelete = fileRepo.delete(1);
        assertThat(isDelete).isFalse();
    }
}