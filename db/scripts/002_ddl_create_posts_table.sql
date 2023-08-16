CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    description VARCHAR NOT NULL,
    creation_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    price BIGINT NOT NULL,
    user_id  INT REFERENCES users(id) NOT NULL
);