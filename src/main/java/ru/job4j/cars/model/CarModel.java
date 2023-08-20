package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "models")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CarModel implements Comparable<CarModel> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String name;
    private String brand;

    @Override
    public int compareTo(@NonNull CarModel o) {
        var rsl = brand.compareTo(o.brand);
        if (rsl == 0) {
            rsl = name.compareTo(o.name);
        }
        return rsl;
    }
}