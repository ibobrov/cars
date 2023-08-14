package ru.job4j.cars.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostPreview {
    @EqualsAndHashCode.Include
    private int id;
    private String title;
    private long carPrice;
    private int odometer;
    private int photoId;
}
