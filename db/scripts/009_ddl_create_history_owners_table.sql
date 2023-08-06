CREATE TABLE history_owners (
    id SERIAL PRIMARY KEY,
    owners_id INT NOT NULL REFERENCES owners(id),
    car_id INT NOT NULL REFERENCES cars(id)
);