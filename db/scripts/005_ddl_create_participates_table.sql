CREATE TABLE participates (
    id serial PRIMARY KEY,
    post_id INT NOT NULL REFERENCES auto_post(id),
    user_id INT NOT NULL REFERENCES auto_user(id),
    UNIQUE (post_id, user_id)
);