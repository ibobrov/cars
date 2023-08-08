package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateEngineRepositoryTest {
    private static SessionFactory sf;
    private static HibernateEngineRepository engineRepo;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        engineRepo = new HibernateEngineRepository(crudRepo);
    }

    @AfterEach
    public void clearRepo() {
        for (var engine : engineRepo.getAll()) {
            engineRepo.delete(engine.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        sf.close();
    }

    @Test
    public void whenSaveEngineThenFindByIdSame() {
        var engine = new Engine(-1, "2JZ-GTE");
        engineRepo.save(engine);
        assertThat(engineRepo.findById(engine.getId()).get())
                .usingRecursiveComparison().isEqualTo(engine);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(engineRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteEngineThenDeleteReturnTrueAndFindByIdEmpty() {
        var engine = new Engine(-1, "2JZ-GTE");
        engineRepo.save(engine);
        var isDelete = engineRepo.delete(engine.getId());
        assertThat(isDelete).isTrue();
        assertThat(engineRepo.findById(engine.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistEngineThenDeleteReturnFalse() {
        var isDelete = engineRepo.delete(1);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenSaveAndUpdateEngineThenFindUpdated() {
        var engine = new Engine(-1, "2JZ-GTE");
        engineRepo.save(engine);
        var engineV2 = new Engine(engine.getId(), "2JZ-GTE + turbine");
        var isUpdate = engineRepo.update(engineV2);
        assertThat(isUpdate).isTrue();
        assertThat(engineRepo.findById(engine.getId()).get())
                .usingRecursiveComparison().isEqualTo(engineV2);
    }

    @Test
    public void whenUpdateDontExistEngineThenReturnFalse() {
        var isUpdate = engineRepo.update(new Engine());
        assertThat(isUpdate).isFalse();
    }
}
