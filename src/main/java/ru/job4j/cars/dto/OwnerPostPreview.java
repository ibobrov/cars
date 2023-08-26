package ru.job4j.cars.dto;

import lombok.Data;

@Data
public class OwnerPostPreview {
    private final PostPreview postPreview;
    private final boolean visibility;
}
