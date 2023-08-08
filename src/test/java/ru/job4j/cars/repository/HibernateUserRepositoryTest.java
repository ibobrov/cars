package ru.job4j.cars.repository;


import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernateUserRepositoryTest {
    private static SessionFactory sf;
    private static HibernateUserRepository userRepo;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        userRepo = new HibernateUserRepository(crudRepo);
    }

    @AfterEach
    public void clearRepo() {
        for (var user : userRepo.getAll()) {
            userRepo.delete(user.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        sf.close();
    }

    @Test
    public void whenSaveUserThenFindByIdSame() {
        var user = User.of().login("name").password("pass").build();
        userRepo.save(user);
        assertThat(userRepo.findById(user.getId()).get()).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(userRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeleteUserThenDeleteReturnTrueAndFindByIdEmpty() {
        var user = User.of().login("name").password("pass").build();
        userRepo.save(user);
        var isDelete = userRepo.delete(user.getId());
        assertThat(isDelete).isTrue();
        assertThat(userRepo.findById(user.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistUserThenDeleteReturnFalse() {
        var isDelete = userRepo.delete(1);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenSaveAndUpdateUserThenFindUpdated() {
        var user = User.of().login("name").password("pass").build();
        userRepo.save(user);
        var userV2 = User.of().id(user.getId()).login("name2").password("pass2").build();
        var isUpdate = userRepo.update(userV2);
        assertThat(isUpdate).isTrue();
        assertThat(userRepo.findById(user.getId()).get()).usingRecursiveComparison().isEqualTo(userV2);
    }

    @Test
    public void whenUpdateDontExistUserThenReturnFalse() {
        var isUpdate = userRepo.update(new User());
        assertThat(isUpdate).isFalse();
    }

    @Test
    public void whenSaveAndFindByLoginPassThenFindSameUser() {
        var user1 = User.of().login("name1").password("pass1").build();
        var user2 = User.of().login("name2").password("pass2").build();
        userRepo.save(user1);
        userRepo.save(user2);
        assertThat(userRepo.findByLoginPassword("name1", "pass1").get())
                .usingRecursiveComparison().isEqualTo(user1);
        assertThat(userRepo.findByLoginPassword("name2", "pass2").get())
                .usingRecursiveComparison().isEqualTo(user2);
    }
}