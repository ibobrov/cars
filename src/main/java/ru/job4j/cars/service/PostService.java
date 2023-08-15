package ru.job4j.cars.service;

import ru.job4j.cars.dto.Filter;
import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Optional<PostDto> findById(int id);

    List<PostPreview> findByFilter(Filter filter);

    List<PostPreview> getRecommendation(int itemCount);

    List<PostPreview> getLastDay();

    List<PostPreview> getAll();
}
