package ru.job4j.cars.service;

import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Optional<PostDto> findById(int id);

    List<PostPreview> getAll();
}
