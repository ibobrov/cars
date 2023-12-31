package ru.job4j.cars.repository;

import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    boolean update(Post post);

    boolean delete(int id);

    Optional<Post> findById(int id);

    List<Post> getAll();

    List<Post> getVisible();

    List<Post> getLastDay();

    List<Post> getWithPhoto();

    List<Post> getBrand(String mark);

    List<Post> findByFilter(Map<String, String> filters);

    List<Post> findByUser(int id);

    boolean hide(int id);
}
