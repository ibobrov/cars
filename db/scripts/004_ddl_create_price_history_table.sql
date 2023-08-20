CREATE TABLE price_histories (
    id SERIAL PRIMARY KEY,
    before BIGINT NOT NULL,
    after BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    post_id int REFERENCES posts(id)
);