CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       first_name VARCHAR(100) NOT NULL,
       last_name VARCHAR(100) NOT NULL,
       age INTEGER CHECK (age >= 0),
       country VARCHAR(100)
);


INSERT INTO users (id,first_name, last_name, age, country)
VALUES
    (1,'John', 'Doe', 35, 'United States'),
    (2,'Emma', 'Smith', 28, 'Canada'),
    (3,'Carlos', 'Rodriguez', 42, 'Mexico'),
    (4,'Sophia', 'Lee', 31, 'South Korea'),
    (5,'Ahmed', 'Hassan', 39, 'Egypt');