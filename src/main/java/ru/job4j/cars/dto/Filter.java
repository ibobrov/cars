package ru.job4j.cars.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
@EqualsAndHashCode
public class Filter {
    private String brand = "none";
    private String model = "none";
    private long fromPrice = 0;
    private long toPrice = 0;
    private boolean withoutPhoto = true;
}
