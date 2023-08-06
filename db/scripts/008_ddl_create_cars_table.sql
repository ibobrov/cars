CREATE TABLE cars (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    owner_id INT NOT NULL REFERENCES owners(id),
    engine_id INT NOT NULL UNIQUE REFERENCES engines(id)
);