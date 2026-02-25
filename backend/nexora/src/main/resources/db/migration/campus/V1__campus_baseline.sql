CREATE TABLE student (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP
);

CREATE TABLE faculty (
    id UUID PRIMARY KEY,
    name VARCHAR(200),
    created_at TIMESTAMP
);