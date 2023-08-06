CREATE TABLE participates (
    id serial PRIMARY KEY,
    post_id INT NOT NULL REFERENCES posts(id),
    user_id INT NOT NULL REFERENCES users(id),
    UNIQUE (post_id, user_id)
);