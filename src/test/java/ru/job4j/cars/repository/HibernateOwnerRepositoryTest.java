package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateOwnerRepositoryTest {
    private static SessionFactory sf;
    private static HibernateOwnerRepository ownerRepo;
    private static HibernateUserRepository userRepo;
    private static User staticUser;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        ownerRepo = new HibernateOwnerRepository(crudRepo);
        userRepo = new HibernateUserRepository(crudRepo);
        staticUser = User.of().login("name").password("pass").build();
        userRepo.save(staticUser);
    }

    @AfterEach
    public void clearRepo() {
        for (var owner : ownerRepo.getAll()) {
            ownerRepo.delete(owner.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        userRepo.delete(staticUser.getId());
        sf.close();
    }

    @Test
    public void whenSaveOwnerThenFindByIdSame() {
        var owner = new Owner(-1, "name", staticUser);
        ownerRepo.save(owner);
        assertThat(ownerRepo.findById(owner.getId()).get())
                .usingRecursiveComparison().isEqualTo(owner);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(ownerRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteOwnerThenDeleteReturnTrueAndFindByIdEmpty() {
        var owner = new Owner(-1, "name", staticUser);
        ownerRepo.save(owner);
        var isDelete = ownerRepo.delete(owner.getId());
        assertThat(isDelete).isTrue();
        assertThat(ownerRepo.findById(owner.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistOwnerThenDeleteReturnFalse() {
        var isDelete = ownerRepo.delete(1);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenSaveAndUpdateOwnerThenFindUpdated() {
        var owner = new Owner(-1, "name", staticUser);
        ownerRepo.save(owner);
        var ownerV2 = new Owner(owner.getId(), "name2", staticUser);
        var isUpdate = ownerRepo.update(ownerV2);
        assertThat(isUpdate).isTrue();
        assertThat(ownerRepo.findById(owner.getId()).get())
                .usingRecursiveComparison().isEqualTo(ownerV2);
    }

    @Test
    public void whenUpdateDontExistOwnerThenReturnFalse() {
        var isUpdate = ownerRepo.update(new Owner());
        assertThat(isUpdate).isFalse();
    }
}
