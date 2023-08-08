CREATE TABLE post_files (
    id SERIAL PRIMARY KEY,
    post_id INT NOT NULL REFERENCES posts(id),
    file_id INT NOT NULL REFERENCES files(id)
);