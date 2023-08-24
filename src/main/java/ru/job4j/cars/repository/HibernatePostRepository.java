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
                                              WHERE p.id = :id
                                              """;
    private final static String GET_ALL = """
                                              SELECT DISTINCT p
                                              FROM Post p
                                              LEFT JOIN FETCH p.user
                                              LEFT JOIN FETCH p.car
                                              LEFT JOIN FETCH p.priceHistories
                                              LEFT JOIN FETCH p.participates
                                              LEFT JOIN FETCH p.files
                                              """;
    private final static BiFunction<CriteriaBuilder, Root<Post>, Predicate>
            WITH_PHOTO = (builder, posts) -> builder.isNotEmpty(posts.get("files"));
    private final static BiFunction<CriteriaBuilder, Root<Post>, Predicate>
            SELECT_LAST_DAY = (builder, posts) -> builder.between(posts.get("creationDate"),
                                                                    now().minusDays(1), now());

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
            rsl = getByCriteria(SELECT_LAST_DAY);
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
    public List<Post> getByFilter(Map<String, String> filters) {
        BiFunction<CriteriaBuilder, Root<Post>, Predicate> biFunction =
                (builder, posts) -> createPredicate(filters, builder, posts);
        return getByCriteria(biFunction);
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

    private Predicate andOrReturn(Predicate one, Predicate two, CriteriaBuilder builder) {
        if (one == null) {
            return two;
        } else {
            return builder.and(one, two);
        }
    }

    private Predicate createPredicate(Map<String, String> filters, CriteriaBuilder builder, Root<Post> posts) {
        Predicate predicate = null;
        var keys = filters.keySet();
        for (var key : keys) {
            switch (key) {
                case "brand" -> {
                    var byBrand = getBrandBiFunc(filters.get("brand")).apply(builder, posts);
                    predicate = andOrReturn(predicate, byBrand, builder);
                }
                case "model" -> {
                    var byModel = getBrandBiFunc(filters.get("model")).apply(builder, posts);
                    predicate = andOrReturn(predicate, byModel, builder);
                }
                case "fromPrice" -> {
                    var byPrice = builder.greaterThanOrEqualTo(posts.get("price"), filters.get("fromPrice"));
                    predicate = andOrReturn(predicate, byPrice, builder);
                }
                case "toPrice" -> {
                    var byPrice = builder.lessThanOrEqualTo(posts.get("price"), filters.get("toPrice"));
                    predicate = andOrReturn(predicate, byPrice, builder);
                }
                case "withoutPhoto" -> {
                    if ("false".equals(filters.get("withoutPhoto"))) {
                        var photoPredicate = WITH_PHOTO.apply(builder, posts);
                        predicate = andOrReturn(predicate, photoPredicate, builder);
                    }
                }
            }
        }
        return predicate;
    }
}
