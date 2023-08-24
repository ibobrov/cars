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
import java.util.Map;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HibernatePostRepositoryTest {
    private static SessionFactory sf;
    private static HibernatePostRepository postRepo;
    private final User user = new User(1);
    private final Car car1 = new Car(1);
    private final Car car2 = new Car(2);
    private final Car car3 = new Car(3);
    private final Car car4 = new Car(4);
    private final File file = new File(1);
    private Post toyotaSupra;
    private Post bmwX5;
    private Post hondaAccord;
    private Post bmwM5;

    @BeforeAll
    public static void initRepo() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        postRepo = new HibernatePostRepository(new CrudRepository(sf));
        ScriptsTool.executeScript(sf, ScriptsTool.getProp("post_refs_init"));
    }

    @AfterEach
    public void clearRepo() {
        for (var post : postRepo.getAll()) {
            postRepo.delete(post.getId());
        }
    }

    @AfterAll
    public static void closeSessionFactory() {
        ScriptsTool.executeScript(sf, ScriptsTool.getProp("post_refs_clear"));
        sf.close();
    }

    @Test
    public void whenSavePostThenFindByIdSame() {
        var post = new Post(-1, "desc", now(), 10000L, user, car1,
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
        var post = new Post(-1, "desc", now(), 10000L, user, car1,
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
        var post = new Post(-1, "desc", now(), 10000L, user, car1,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post);
        var postV2 = new Post(post.getId(), "new desc", now(), 10000L, user, car1,
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
        var post1 = new Post(-1, "desc", now(), 10000L, user, car1,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, user, car2,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getBrand("toyota")).isEqualTo(List.of(post1));
        assertThat(postRepo.getBrand("bmw")).isEqualTo(List.of(post2));
    }

    @Test
    public void whenGetWithPhotoThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now(), 10000L, user, car1,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, user, car2,
                Set.of(), Set.of(), Set.of(file));
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getWithPhoto()).isEqualTo(List.of(post2));
    }

    @Test
    public void whenGetByDayThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 10000L, user, car1,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, user, car2,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getLastDay()).isEqualTo(List.of(post2));
    }

    @Test
    public void whenFindByFilterOnlyWithPhoto() {
        prepareToFindByFilters();
        var onlyWithPhoto = Map.of("withoutPhoto", "false");
        assertThat(postRepo.getByFilter(onlyWithPhoto)).isEqualTo(List.of(toyotaSupra, bmwM5));
    }

    @Test
    public void whenFindByFilterOnlyBmw() {
        prepareToFindByFilters();
        var onlyBmw = Map.of("brand", "bmw");
        assertThat(postRepo.getByFilter(onlyBmw)).isEqualTo(List.of(bmwX5, bmwM5));
    }

    @Test
    public void whenFindByFilterOnlyBmwWithNoPhoto() {
        prepareToFindByFilters();
        var anyBmw = Map.of("withoutPhoto", "true", "brand", "bmw");
        assertThat(postRepo.getByFilter(anyBmw)).isEqualTo(List.of(bmwX5, bmwM5));
    }

    @Test
    public void whenFindByFilterOnlyBmwWithPhoto() {
        prepareToFindByFilters();
        var bmwWithPhoto = Map.of("withoutPhoto", "false", "brand", "bmw");
        assertThat(postRepo.getByFilter(bmwWithPhoto)).isEqualTo(List.of(bmwM5));
    }

    @Test
    public void whenFindByFilterOnlyModelWith5InName() {
        prepareToFindByFilters();
        var modelFive = Map.of("model", "5");
        assertThat(postRepo.getByFilter(modelFive)).isEqualTo(List.of(bmwX5, bmwM5));
    }

    @Test
    public void whenFindByFilterOnlyModelWithAccordInName() {
        prepareToFindByFilters();
        var modelAccord = Map.of("model", "accord");
        assertThat(postRepo.getByFilter(modelAccord)).isEqualTo(List.of(hondaAccord));
    }

    @Test
    public void whenFindByPriceGreater10k() {
        prepareToFindByFilters();
        var from10kPlus = Map.of("fromPrice", "10000");
        assertThat(postRepo.getByFilter(from10kPlus)).isEqualTo(List.of(toyotaSupra, bmwX5, hondaAccord, bmwM5));
    }

    @Test
    public void whenFindByPriceGreater16k() {
        prepareToFindByFilters();
        var from16kPlus = Map.of("fromPrice", "16000");
        assertThat(postRepo.getByFilter(from16kPlus)).isEqualTo(List.of(hondaAccord, bmwM5));
    }

    @Test
    public void whenFindByPriceLess20k() {
        prepareToFindByFilters();
        var to20k = Map.of("toPrice", "20000");
        assertThat(postRepo.getByFilter(to20k)).isEqualTo(List.of(toyotaSupra, bmwX5, hondaAccord, bmwM5));
    }

    @Test
    public void whenFindByPriceLess15k() {
        prepareToFindByFilters();
        var to15k = Map.of("toPrice", "15000");
        assertThat(postRepo.getByFilter(to15k)).isEqualTo(List.of(toyotaSupra, bmwX5));
    }

    @Test
    public void whenFindByPriceFrom15kTo16k() {
        prepareToFindByFilters();
        var like15or16k = Map.of("fromPrice", "15000", "toPrice", "16000");
        assertThat(postRepo.getByFilter(like15or16k)).isEqualTo(List.of(bmwX5, hondaAccord));
    }

    @Test
    public void whenFindByPriceFrom15kTo16kAndHonda() {
        prepareToFindByFilters();
        var like15or16kAndHonda = Map.of("fromPrice", "15000", "toPrice", "16000", "brand", "honda");
        assertThat(postRepo.getByFilter(like15or16kAndHonda)).isEqualTo(List.of(hondaAccord));
    }

    @Test
    public void whenFindByPriceFrom15kTo16kAndHondaAndWithPhoto() {
        prepareToFindByFilters();
        var like15or16kAndHondaAndPhoto = Map.of("fromPrice", "15000", "toPrice", "16000",
                                                "brand", "honda", "withoutPhoto", "false");
        assertThat(postRepo.getByFilter(like15or16kAndHondaAndPhoto)).isEqualTo(List.of());
    }

    private void prepareToFindByFilters() {
        toyotaSupra = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 10000L, user, car1,
                Set.of(), Set.of(), Set.of(file));
        bmwX5 = new Post(-1, "desc", now(), 15000L, user, car2,
                Set.of(), Set.of(), Set.of());
        hondaAccord = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 16000L, user, car3,
                Set.of(), Set.of(), Set.of());
        bmwM5 = new Post(-1, "desc", now(), 20000L, user, car4,
                Set.of(), Set.of(), Set.of(file));
        postRepo.save(toyotaSupra);
        postRepo.save(bmwX5);
        postRepo.save(hondaAccord);
        postRepo.save(bmwM5);
    }
}