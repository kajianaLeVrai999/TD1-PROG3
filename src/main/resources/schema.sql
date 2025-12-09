CREATE TABLE product (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price NUMERIC(10,2) NOT NULL,
                         creation_datetime TIMESTAMP NOT NULL
);


CREATE TABLE product_category (
                                  id SERIAL PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  product_id INT REFERENCES product(id)
);


GRANT CREATE ON SCHEMA public TO product_manager_user;