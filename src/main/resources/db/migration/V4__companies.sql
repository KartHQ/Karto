CREATE TABLE companies (
    id CHAR(36) NOT NULL,
    state_code CHAR(2) NOT NULL,
    registration_number VARCHAR(20) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    email VARCHAR(256) NOT NULL,
    phone VARCHAR(22) NOT NULL,
    password VARCHAR NOT NULL,
    secret_key VARCHAR(28) NOT NULL,
    counter BIGINT NOT NULL,
    status VARCHAR(7) NOT NULL CHECK (status IN ('PENDING', 'ACTIVE')),
    expiration_period_days INTEGER NOT NULL,
    max_usage_count INTEGER NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX company_name_index ON companies (company_name);

CREATE UNIQUE INDEX company_email_index ON companies (email);

CREATE UNIQUE INDEX company_phone_index ON companies (phone);

CREATE UNIQUE INDEX company_registration_number_index ON companies (registration_number);