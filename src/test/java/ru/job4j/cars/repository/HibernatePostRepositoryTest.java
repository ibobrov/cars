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
    private final Car toyotaSupra = new Car(0, "toyota supra", 1998, 62563,
            new Engine(1), new Owner(1), Set.of());
    private final Car bmwX5 = new Car(0, "bmw x5", 2006, 12543,
            new Engine(2), new Owner(1), Set.of());
    private final Car hondaAccord = new Car(0, "honda accord", 2010, 54543,
            new Engine(3), new Owner(1), Set.of());
    private final Car bmwM5 = new Car(0, "bmw m5", 2020, 23568,
            new Engine(4), new Owner(1), Set.of());
    private Post toyotaSupraPost;
    private Post bmwX5Post;
    private Post hondaAccordPost;
    private Post bmwM5Post;

    private final File file = new File(1);

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
        var post = new Post(-1, "desc", now(), 10000L, user, toyotaSupra,
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
        var post = new Post(-1, "desc", now(), 10000L, user, toyotaSupra,
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
        var post = new Post(-1, "desc", now(), 10000L, user, toyotaSupra,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post);
        var postV2 = new Post(post.getId(), "new desc", now(), 10000L, user, toyotaSupra,
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
        var post1 = new Post(-1, "desc", now(), 10000L, user, toyotaSupra,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, user, bmwX5,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getBrand("toyota")).isEqualTo(List.of(post1));
        assertThat(postRepo.getBrand("bmw")).isEqualTo(List.of(post2));
    }

    @Test
    public void whenGetWithPhotoThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now(), 10000L, user, toyotaSupra,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, user, bmwX5,
                Set.of(), Set.of(), Set.of(file));
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getWithPhoto()).isEqualTo(List.of(post2));
    }

    @Test
    public void whenGetByDayThenReturnCorrect() {
        var post1 = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 10000L, user, toyotaSupra,
                Set.of(), Set.of(), Set.of());
        var post2 = new Post(-1, "desc", now(), 10000L, user, bmwX5,
                Set.of(), Set.of(), Set.of());
        postRepo.save(post1);
        postRepo.save(post2);
        assertThat(postRepo.getLastDay()).isEqualTo(List.of(post2));
    }

    @Test
    public void whenFindByFilterOnlyWithPhoto() {
        prepareToFindByFilters();
        var onlyWithPhoto = Map.of("withoutPhoto", "false");
        assertThat(postRepo.findByFilter(onlyWithPhoto)).isEqualTo(List.of(toyotaSupraPost, bmwM5Post));
    }

    @Test
    public void whenFindByFilterOnlyBmw() {
        prepareToFindByFilters();
        var onlyBmw = Map.of("brand", "bmw");
        assertThat(postRepo.findByFilter(onlyBmw)).isEqualTo(List.of(bmwX5Post, bmwM5Post));
    }

    @Test
    public void whenFindByFilterOnlyBmwWithNoPhoto() {
        prepareToFindByFilters();
        var anyBmw = Map.of("withoutPhoto", "true", "brand", "bmw");
        assertThat(postRepo.findByFilter(anyBmw)).isEqualTo(List.of(bmwX5Post, bmwM5Post));
    }

    @Test
    public void whenFindByFilterOnlyBmwWithPhoto() {
        prepareToFindByFilters();
        var bmwWithPhoto = Map.of("withoutPhoto", "false", "brand", "bmw");
        assertThat(postRepo.findByFilter(bmwWithPhoto)).isEqualTo(List.of(bmwM5Post));
    }

    @Test
    public void whenFindByFilterOnlyModelWith5InName() {
        prepareToFindByFilters();
        var modelFive = Map.of("model", "5");
        assertThat(postRepo.findByFilter(modelFive)).isEqualTo(List.of(bmwX5Post, bmwM5Post));
    }

    @Test
    public void whenFindByFilterOnlyModelWithAccordInName() {
        prepareToFindByFilters();
        var modelAccord = Map.of("model", "accord");
        assertThat(postRepo.findByFilter(modelAccord)).isEqualTo(List.of(hondaAccordPost));
    }

    @Test
    public void whenFindByPriceGreater10k() {
        prepareToFindByFilters();
        var from10kPlus = Map.of("fromPrice", "10000");
        assertThat(postRepo.findByFilter(from10kPlus)).isEqualTo(List.of(toyotaSupraPost, bmwX5Post, hondaAccordPost, bmwM5Post));
    }

    @Test
    public void whenFindByPriceGreater16k() {
        prepareToFindByFilters();
        var from16kPlus = Map.of("fromPrice", "16000");
        assertThat(postRepo.findByFilter(from16kPlus)).isEqualTo(List.of(hondaAccordPost, bmwM5Post));
    }

    @Test
    public void whenFindByPriceLess20k() {
        prepareToFindByFilters();
        var to20k = Map.of("toPrice", "20000");
        assertThat(postRepo.findByFilter(to20k)).isEqualTo(List.of(toyotaSupraPost, bmwX5Post, hondaAccordPost, bmwM5Post));
    }

    @Test
    public void whenFindByPriceLess15k() {
        prepareToFindByFilters();
        var to15k = Map.of("toPrice", "15000");
        assertThat(postRepo.findByFilter(to15k)).isEqualTo(List.of(toyotaSupraPost, bmwX5Post));
    }

    @Test
    public void whenFindByPriceFrom15kTo16k() {
        prepareToFindByFilters();
        var like15or16k = Map.of("fromPrice", "15000", "toPrice", "16000");
        assertThat(postRepo.findByFilter(like15or16k)).isEqualTo(List.of(bmwX5Post, hondaAccordPost));
    }

    @Test
    public void whenFindByPriceFrom15kTo16kAndHonda() {
        prepareToFindByFilters();
        var like15or16kAndHonda = Map.of("fromPrice", "15000", "toPrice", "16000", "brand", "honda");
        assertThat(postRepo.findByFilter(like15or16kAndHonda)).isEqualTo(List.of(hondaAccordPost));
    }

    @Test
    public void whenFindByPriceFrom15kTo16kAndHondaAndWithPhoto() {
        prepareToFindByFilters();
        var like15or16kAndHondaAndPhoto = Map.of("fromPrice", "15000", "toPrice", "16000",
                                                "brand", "honda", "withoutPhoto", "false");
        assertThat(postRepo.findByFilter(like15or16kAndHondaAndPhoto)).isEqualTo(List.of());
    }

    private void prepareToFindByFilters() {
        toyotaSupraPost = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 10000L, user, toyotaSupra,
                Set.of(), Set.of(), Set.of(file));
        bmwX5Post = new Post(-1, "desc", now(), 15000L, user, bmwX5,
                Set.of(), Set.of(), Set.of());
        hondaAccordPost = new Post(-1, "desc", now().minusDays(1).minusMinutes(1), 16000L, user, hondaAccord,
                Set.of(), Set.of(), Set.of());
        bmwM5Post = new Post(-1, "desc", now(), 20000L, user, bmwM5,
                Set.of(), Set.of(), Set.of(file));
        postRepo.save(toyotaSupraPost);
        postRepo.save(bmwX5Post);
        postRepo.save(hondaAccordPost);
        postRepo.save(bmwM5Post);
    }
}