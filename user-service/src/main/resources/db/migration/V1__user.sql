CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    second_name VARCHAR(255),
    register_at TIMESTAMP NOT NULL
);