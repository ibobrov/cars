package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return getByCriteria((builder, query, posts) -> query.select(posts)
                .distinct(true)
                .where(
                        builder.between(
                                posts.get("creationDate"),
                                now().minusDays(1), now()
                        )));
    }

    @Override
    public List<Post> getWithPhoto() {
        return getByCriteria((builder, query, posts) -> query.select(posts)
                .distinct(true)
                .where(
                        builder.isNotEmpty(
                                posts.get("files")
                        )));
    }

    @Override
    public List<Post> getBrand(String brand) {
        return getByCriteria((builder, query, posts) -> query.select(posts)
                .distinct(true)
                .where(
                        builder.like(
                                posts.get("car").get("name"),
                                "%" + brand + "%"
                        )));
    }

    private List<Post> getByCriteria(TriConsumer<CriteriaBuilder, CriteriaQuery<Post>, Root<Post>> consumer) {
        List<Post> rsl = List.of();
        try {
            rsl = crudRepo.runAndBack(session -> {
                var criteriaBuilder = session.getEntityManagerFactory().getCriteriaBuilder();
                var criteriaQuery = criteriaBuilder.createQuery(Post.class);
                var posts = criteriaQuery.from(Post.class);
                posts.fetch("user", JoinType.LEFT);
                posts.fetch("car", JoinType.LEFT);
                posts.fetch("priceHistories", JoinType.LEFT);
                posts.fetch("participates", JoinType.LEFT);
                posts.fetch("files", JoinType.LEFT);
                consumer.accept(criteriaBuilder, criteriaQuery, posts);
                return session.createQuery(criteriaQuery).getResultList();
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rsl;
    }
}
