package ru.job4j.cars.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "engines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Engine implements Comparable<Engine> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String name;

    public Engine(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NonNull Engine o) {
        return name.compareTo(o.name);
    }
}
