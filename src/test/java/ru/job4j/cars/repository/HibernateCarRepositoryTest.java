package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;

import java.util.Set;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateCarRepositoryTest {
    private static SessionFactory sf;
    private static HibernateCarRepository carRepo;
    private final Engine engine = new Engine(1);
    private final Owner owner1 = new Owner(1);
    private final Owner owner2 = new Owner(2);

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        carRepo = new HibernateCarRepository(new CrudRepository(sf));
        ScriptsTool.executeScript(sf, ScriptsTool.getProp("car_refs_init"));
    }

    @AfterEach
    public void clearRepo() {
        for (var car : carRepo.getAll()) {
            carRepo.delete(car.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        ScriptsTool.executeScript(sf, ScriptsTool.getProp("car_refs_clear"));
        sf.close();
    }

    @Test
    public void whenSaveCarThenFindByIdSame() {
        var car = new Car(-1, "toyota supra", 1998, 62563, engine, owner1,
                Set.of(owner1, owner2));
        carRepo.save(car);
        assertThat(carRepo.findById(car.getId()).get()).isEqualTo(car);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(carRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteCarThenDeleteReturnTrueAndFindByIdEmpty() {
        var car = new Car(-1, "toyota supra", 1998, 62563, engine, owner1,
                Set.of(owner1, owner2));
        carRepo.save(car);
        var isDelete = carRepo.delete(car.getId());
        assertThat(isDelete).isTrue();
        assertThat(carRepo.findById(car.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistCarThenDeleteReturnFalse() {
        var isDelete = carRepo.delete(1);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenSaveAndUpdateCarThenFindUpdated() {
        var car = new Car(-1, "toyota supra", 1998, 62563, engine, owner1,
                Set.of(owner1));
        carRepo.save(car);
        var carV2 = new Car(car.getId(), "toyota supra", 1998, 62563, engine, owner2,
                Set.of(owner1, owner2));
        var isUpdate = carRepo.update(carV2);
        assertThat(isUpdate).isTrue();
        assertThat(carRepo.findById(car.getId()).get()).isEqualTo(carV2);
    }

    @Test
    public void whenUpdateDontExistCarThenReturnFalse() {
        var isUpdate = carRepo.update(new Car());
        assertThat(isUpdate).isFalse();
    }
}