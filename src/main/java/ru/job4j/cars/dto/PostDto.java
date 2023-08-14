package ru.job4j.cars.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostDto {
    @EqualsAndHashCode.Include
    private int id;
    private String description;
    private LocalDateTime creationDate;
    private int countOwners;
    private String carName;
    private long carPrice;
    private int carYear;
    private int carOdometer;
    private String carEngine;
    private boolean isOwner;
    private List<Integer> photoIds;
}
