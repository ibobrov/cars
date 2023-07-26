CREATE TABLE auto_post (
    id SERIAL PRIMARY KEY,
    description VARCHAR NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    auto_user_id  INT REFERENCES auto_user(id) NOT NULL
);