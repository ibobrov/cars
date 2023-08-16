package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;

import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernatePostRepositoryTest {
    private static SessionFactory sf;
    private static HibernatePostRepository postRepo;
    private static HibernateCarRepository carRepo;
    private static HibernateUserRepository userRepo;
    private static HibernateOwnerRepository ownerRepo;
    private static HibernateEngineRepository engineRepo;
    private static HibernateFileRepository fileRepo;
    private static User staticUser;
    private static Owner staticOwner;
    private static Engine staticEngine1;
    private static Engine staticEngine2;
    private static Car staticCar1;
    private static Car staticCar2;
    private static File staticFile;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        var crudRepo = new CrudRepository(sf);
        postRepo = new HibernatePostRepository(crudRepo);
        carRepo = new HibernateCarRepository(crudRepo);
        userRepo = new HibernateUserRepository(crudRepo);
        ownerRepo = new HibernateOwnerRepository(crudRepo);
        userRepo = new HibernateUserRepository(crudRepo);
        engineRepo = new HibernateEngineRepository(crudRepo);
        fileRepo = new HibernateFileRepository(crudRepo);
        staticUser = User.of().id(-1).login("user").password("pass").build();
        userRepo.save(staticUser);
        staticOwner = new Owner(-1, "owner", staticUser);
        ownerRepo.save(staticOwner);
        staticEngine1 = new Engine(-1, "2JZ-GTE");
        staticEngine2 = new Engine(-1, "4,6is");
        engineRepo.save(staticEngine1);
        engineRepo.save(staticEngine2);
        staticCar1 = new Car(-1, "toyota supra", 1998, 62563, staticEngine1,
                staticOwner, Set.of(staticOwner));
        staticCar2 = new Car(-1, "bmw x5", 2006, 12543, staticEngine2,
                staticOwner, Set.of(staticOwner));
        carRepo.save(staticCar1);
        carRepo.save(staticCar2);
        staticFile = new File(-1, "name", "path");
        fileRepo.save(staticFile);

    }

    @AfterEach
    public void clearRepo() {
        for (var post : postRepo.getAll()) {
            postRepo.delete(post.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        carRepo.delete(staticCar1.getId());
        fileRepo.delete(staticFile.getId());
        carRepo.delete(staticCar2.getId());
        ownerRepo.delete(staticOwner.getId());
        userRepo.delete(staticUser.getId());
        engineRepo.delete(staticEngine1.getId());
        engineRepo.delete(staticEngine2.getId());
        sf.close();
    }

    @Test
    public void whenSavePostThenFindByIdSame() {
        var post = new Post(-1, "desc", now(), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post);
        assertThat(postRepo.findById(post.getId()).get()).isEqualTo(post);
    }

    @Test
    public void whenFindByNonExistIdThenReturnEmpty() {
        assertThat(postRepo.findById(1)).isEqualTo(empty());
    }

    @Test
    public void whenSaveAndDeletePostThenDeleteReturnTrueAndFindByIdEmpty() {
        var post = new Post(-1, "desc", now(), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post);
        var isDelete = postRepo.delete(post.getId());
        assertThat(isDelete).isTrue();
        assertThat(postRepo.findById(post.getId())).isEqualTo(empty());
    }

    @Test
    public void whenDeleteNonExistPostThenDeleteReturnFalse() {
        var isDelete = postRepo.delete(1);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenSaveAndUpdatePostThenFindUpdated() {
        var post = new Post(-1, "desc", now(), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post);
        var postV2 = new Post(post.getId(), "new desc", now(), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        var isUpdate = postRepo.update(postV2);
        assertThat(isUpdate).isTrue();
        var actualPost = postRepo.findById(post.getId()).get();
        assertThat(actualPost).isEqualTo(postV2);
        assertThat(actualPost.getDescription()).isEqualTo("new desc");
    }

    @Test
    public void whenUpdateDontExistPostThenReturnFalse() {
        var isUpdate = postRepo.update(new Post());
        assertThat(isUpdate).isFalse();
    }

    @Test
    public void whenGetBrandThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now(), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, staticUser, staticCar2,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getBrand("toyota")).isEqualTo(List.of(post1));
        assertThat(postRepo.getBrand("bmw")).isEqualTo(List.of(post2));
    }

    @Test
    public void whenGetWithPhotoThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now(), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, staticUser, staticCar2,
                Set.of(), Set.of(), Set.of(staticFile));
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getWithPhoto()).isEqualTo(List.of(post2));
    }

    @Test
    public void whenGetByDayThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 10000L, staticUser, staticCar1,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, staticUser, staticCar2,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getLastDay()).isEqualTo(List.of(post2));
    }
}