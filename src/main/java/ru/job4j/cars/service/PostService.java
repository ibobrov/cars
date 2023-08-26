package ru.job4j.cars.service;

import ru.job4j.cars.dto.*;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostService {

    boolean create(NewPostDto newPost, User user, List<FileDto> files);

    Optional<PostDto> findById(int id);

    List<PostPreview> findByFilter(Map<String, String> filters);

    List<OwnerPostPreview> findByUser(int id);

    List<PostPreview> getRecommendation(int itemCount);

    List<PostPreview> getLastDay();

    List<PostPreview> getAll();

    boolean hide(int id);
}
