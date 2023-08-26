package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;

@Repository
@AllArgsConstructor
public class HibernatePostRepository implements PostRepository {
    private final Logger logger = LoggerFactory.getLogger(HibernatePostRepository.class);
    private final CrudRepository crudRepo;
    private final static String FIND_BY_ID = """
                                              SELECT DISTINCT p
                                              FROM Post p
                                              LEFT JOIN FETCH p.user
                                              LEFT JOIN FETCH p.car
                                              LEFT JOIN FETCH p.priceHistories
                                              LEFT JOIN FETCH p.participates
                                              LEFT JOIN FETCH p.files
                                              WHERE p.id = :id AND p.visibility = true
                                              """;
    private final static String GET_ALL = """
                                              SELECT DISTINCT p
                                              FROM Post p
                                              LEFT JOIN FETCH p.user
                                              LEFT JOIN FETCH p.car
                                              LEFT JOIN FETCH p.priceHistories
                                              LEFT JOIN FETCH p.participates
                                              LEFT JOIN FETCH p.files
                                              ORDER BY p.id DESC
                                              """;
    private final static String GET_VISIBLE = """
                                              SELECT DISTINCT p
                                              FROM Post p
                                              LEFT JOIN FETCH p.user
                                              LEFT JOIN FETCH p.car
                                              LEFT JOIN FETCH p.priceHistories
                                              LEFT JOIN FETCH p.participates
                                              LEFT JOIN FETCH p.files
                                              WHERE p.visibility = true
                                              ORDER BY p.id DESC
                                              """;
    private final static String GET_LAST_DAY = """
                                              SELECT DISTINCT p
                                              FROM Post p
                                              LEFT JOIN FETCH p.user
                                              LEFT JOIN FETCH p.car
                                              LEFT JOIN FETCH p.priceHistories
                                              LEFT JOIN FETCH p.participates
                                              LEFT JOIN FETCH p.files
                                              WHERE p.creationDate BETWEEN :start AND :end AND p.visibility = true
                                              ORDER BY p.id DESC
                                              """;
    private final static String FIND_BY_USER = """
                                              SELECT DISTINCT p
                                              FROM Post p
                                              LEFT JOIN FETCH p.user
                                              LEFT JOIN FETCH p.car
                                              LEFT JOIN FETCH p.priceHistories
                                              LEFT JOIN FETCH p.participates
                                              LEFT JOIN FETCH p.files
                                              WHERE p.user.id = :id
                                              ORDER BY p.id DESC
                                              """;
    private final static BiFunction<CriteriaBuilder, Root<Post>, Predicate>
            WITH_PHOTO = (builder, posts) -> builder.isNotEmpty(posts.get("files"));

    @Override
    public Post save(Post post) {
        try {
            crudRepo.run(session -> session.save(post));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return post;
    }

    @Override
    public boolean update(Post post) {
        var rsl = false;
        try {
            crudRepo.run(session -> session.update(post));
            rsl = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public boolean delete(int id) {
        var rsl = false;
        try {
            rsl = crudRepo.executeUpdate("DELETE FROM Post WHERE id = :id", Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public Optional<Post> findById(int id) {
        Optional<Post> rsl = empty();
        try {
            rsl = crudRepo.optional(FIND_BY_ID, Post.class, Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Post> getVisible() {
        List<Post> rsl = List.of();
        try {
            rsl = crudRepo.query(GET_VISIBLE, Post.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = List.of();
        try {
            rsl = crudRepo.query(GET_ALL, Post.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Post> getLastDay() {
        List<Post> rsl = List.of();
        try {
            rsl = crudRepo.query(GET_LAST_DAY, Post.class,
                    Map.of("start", now().minusDays(1), "end", now()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Post> getWithPhoto() {
        List<Post> rsl = List.of();
        try {
            rsl = getByCriteria(WITH_PHOTO);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public List<Post> getBrand(String brand) {
        List<Post> rsl = List.of();
        try {
            rsl = getByCriteria(getBrandBiFunc(brand));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    private BiFunction<CriteriaBuilder, Root<Post>, Predicate> getBrandBiFunc(String brand) {
        return (builder, posts) -> builder.like(posts.get("car").get("name"), "%" + brand + "%");
    }

    @Override
    public List<Post> findByFilter(Map<String, String> filters) {
        BiFunction<CriteriaBuilder, Root<Post>, Predicate> biFunction =
                (builder, posts) -> createPredicate(filters, builder, posts);
        return getByCriteria(biFunction);
    }

    @Override
    public List<Post> findByUser(int id) {
        List<Post> rsl = List.of();
        try {
            rsl = crudRepo.query(FIND_BY_USER, Post.class,
                    Map.of("id", id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    @Override
    public boolean hide(int id) {
        var rsl = false;
        try {
            rsl = crudRepo.executeUpdate("UPDATE Post p SET p.visibility = false WHERE id = :id",
                    Map.of("id", id)) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    private List<Post> getByCriteria(BiFunction<CriteriaBuilder, Root<Post>, Predicate> biFunction) {
        List<Post> rsl = List.of();
        try {
            rsl = crudRepo.runAndBack(session -> {
                var builder = session.getEntityManagerFactory().getCriteriaBuilder();
                var query = builder.createQuery(Post.class);
                var posts = query.from(Post.class);
                fetchAll(posts);
                var predicate = biFunction.apply(builder, posts);
                query.distinct(true).where(predicate);
                return session.createQuery(query).getResultList();
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }

    private void fetchAll(Root<Post> posts) {
        posts.fetch("user", JoinType.LEFT);
        posts.fetch("car", JoinType.LEFT);
        posts.fetch("priceHistories", JoinType.LEFT);
        posts.fetch("participates", JoinType.LEFT);
        posts.fetch("files", JoinType.LEFT);
    }

    private Predicate createPredicate(Map<String, String> filters, CriteriaBuilder builder, Root<Post> posts) {
        var predicate = builder.isTrue(posts.get("visibility"));
        var keys = filters.keySet();
        for (var key : keys) {
            switch (key) {
                case "brand" -> {
                    var byBrand = getBrandBiFunc(filters.get("brand")).apply(builder, posts);
                    predicate = builder.and(predicate, byBrand);
                }
                case "model" -> {
                    var byModel = getBrandBiFunc(filters.get("model")).apply(builder, posts);
                    predicate = builder.and(predicate, byModel);
                }
                case "fromPrice" -> {
                    var byMinPrice = builder.greaterThanOrEqualTo(posts.get("price"), filters.get("fromPrice"));
                    predicate = builder.and(predicate, byMinPrice);
                }
                case "toPrice" -> {
                    var byMaxPrice = builder.lessThanOrEqualTo(posts.get("price"), filters.get("toPrice"));
                    predicate = builder.and(predicate, byMaxPrice);
                }
                case "withoutPhoto" -> {
                    if ("false".equals(filters.get("withoutPhoto"))) {
                        var photoPredicate = WITH_PHOTO.apply(builder, posts);
                        predicate = builder.and(predicate, photoPredicate);
                    }
                }
            }
        }
        return predicate;
    }
}
