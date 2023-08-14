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
import ru.job4j.cars.model.User;

import java.util.Set;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateCarRepositoryTest {
    private static SessionFactory sf;
    private static HibernateCarRepository carRepo;
    private static HibernateOwnerRepository ownerRepo;
    private static HibernateUserRepository userRepo;
    private static HibernateEngineRepository engineRepo;
    private static User staticUser1;
    private static User staticUser2;
    private static Owner staticOwner1;
    private static Owner staticOwner2;
    private static Engine staticEngine;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        carRepo = new HibernateCarRepository(crudRepo);
        userRepo = new HibernateUserRepository(crudRepo);
        ownerRepo = new HibernateOwnerRepository(crudRepo);
        userRepo = new HibernateUserRepository(crudRepo);
        engineRepo = new HibernateEngineRepository(crudRepo);
        staticUser1 = User.of().id(-1).login("user1").password("pass").build();
        staticUser2 = User.of().id(-1).login("user2").password("pass").build();
        userRepo.save(staticUser1);
        userRepo.save(staticUser2);
        staticOwner1 = new Owner(-1, "owner1", staticUser1);
        staticOwner2 = new Owner(-1, "owner2", staticUser2);
        ownerRepo.save(staticOwner1);
        ownerRepo.save(staticOwner2);
        staticEngine = new Engine(-1, "2JZ-GTE");
        engineRepo.save(staticEngine);
    }

    @AfterEach
    public void clearRepo() {
        for (var car : carRepo.getAll()) {
            carRepo.delete(car.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        ownerRepo.delete(staticOwner1.getId());
        ownerRepo.delete(staticOwner2.getId());
        userRepo.delete(staticUser1.getId());
        userRepo.delete(staticUser2.getId());
        engineRepo.delete(staticEngine.getId());
        sf.close();
    }

    @Test
    public void whenSaveCarThenFindByIdSame() {
        var car = new Car(-1, "toyota supra", 1998, 62563, staticEngine, staticOwner1,
                Set.of(staticOwner1, staticOwner2));
        carRepo.save(car);
        assertThat(carRepo.findById(car.getId()).get()).isEqualTo(car);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(carRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteCarThenDeleteReturnTrueAndFindByIdEmpty() {
        var car = new Car(-1, "toyota supra", 1998, 62563, staticEngine, staticOwner1,
                Set.of(staticOwner1, staticOwner2));
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
        var car = new Car(-1, "toyota supra", 1998, 62563, staticEngine, staticOwner1,
                Set.of(staticOwner1));
        carRepo.save(car);
        var carV2 = new Car(car.getId(), "toyota supra", 1998, 62563, staticEngine, staticOwner2,
                Set.of(staticOwner1, staticOwner2));
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