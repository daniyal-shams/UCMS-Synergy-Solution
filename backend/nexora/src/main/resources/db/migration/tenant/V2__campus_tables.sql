CREATE TABLE campus (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    schema_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE campus_admin_user (
    id UUID PRIMARY KEY,
    campus_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_campus
        FOREIGN KEY(campus_id)
        REFERENCES campus(id)
);