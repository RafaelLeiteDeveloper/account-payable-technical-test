CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    due_date TIMESTAMP NOT NULL,
    payment_date TIMESTAMP,
    amount DECIMAL(15, 2) NOT NULL,
    description VARCHAR(50) NOT NULL,
    status VARCHAR(10) NOT NULL
);

ALTER SEQUENCE account_id_seq RESTART WITH 3;