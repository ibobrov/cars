package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.CarModel;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateCarModelRepositoryTest {
    private static SessionFactory sf;
    private static HibernateCarModelRepository carModelRepo;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        carModelRepo = new HibernateCarModelRepository(crudRepo);
    }

    @AfterEach
    public void clearRepo() {
        for (var engine : carModelRepo.getAll()) {
            carModelRepo.delete(engine.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        sf.close();
    }

    @Test
    public void whenSaveCarModelThenFindByIdSame() {
        var carModel = new CarModel(0, "name", "brand");
        carModelRepo.save(carModel);
        assertThat(carModelRepo.findById(carModel.getId()).get())
                .usingRecursiveComparison().isEqualTo(carModel);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(carModelRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteCarModelThenDeleteReturnTrueAndFindByIdEmpty() {
        var carModel = new CarModel(0, "name", "brand");
        carModelRepo.save(carModel);
        var isDelete = carModelRepo.delete(carModel.getId());
        assertThat(isDelete).isTrue();
        assertThat(carModelRepo.findById(carModel.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistEngineThenDeleteReturnFalse() {
        var isDelete = carModelRepo.delete(1);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenSaveAndUpdateCarModelThenFindUpdated() {
        var carModel = new CarModel(0, "name", "brand");
        carModelRepo.save(carModel);
        var carModelV2 = new CarModel(carModel.getId(), "name2", "brand");
        var isUpdate = carModelRepo.update(carModelV2);
        assertThat(isUpdate).isTrue();
        assertThat(carModelRepo.findById(carModel.getId()).get())
                .usingRecursiveComparison().isEqualTo(carModelV2);
    }

    @Test
    public void whenUpdateDontExistCarModelThenReturnFalse() {
        var isUpdate = carModelRepo.update(new CarModel());
        assertThat(isUpdate).isFalse();
    }
}