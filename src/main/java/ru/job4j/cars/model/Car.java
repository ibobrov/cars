package ru.job4j.cars.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String name;
    @Column(name = "car_year")
    private int year;
    private int odometer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id")
    private Engine engine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "history_owners")
    private Set<Owner> owners = new HashSet<>();
}
