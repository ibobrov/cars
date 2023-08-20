package ru.job4j.cars.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
public class NewPostDto {
    private String carModel;
    private int carYear;
    private long price;
    private int carOdometer;
    private String description;
    private int engineId;
    private List<Integer> photoIds = new ArrayList<>();
}
