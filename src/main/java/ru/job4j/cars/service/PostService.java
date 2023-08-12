package ru.job4j.cars.service;

import ru.job4j.cars.dto.PostPreview;

import java.util.List;

public interface PostService {

    List<PostPreview> getAll();
}
