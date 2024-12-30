CREATE TABLE audit_import (
    id SERIAL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    id_process VARCHAR(50) NOT NULL
);