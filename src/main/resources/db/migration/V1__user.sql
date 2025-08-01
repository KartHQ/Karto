CREATE TABLE user_account (
    id CHAR(36) NOT NULL,
    firstname VARCHAR(48) NOT NULL,
    surname VARCHAR(56) NOT NULL,
    phone VARCHAR(22),
    email VARCHAR(256) NOT NULL,
    password VARCHAR,
    birth_date TIMESTAMP NOT NULL,
    is_verified BOOLEAN NOT NULL,
    is_2fa_enabled BOOLEAN NOT NULL,
    is_banned BOOLEAN NOT NULL,
    secret_key VARCHAR(28) NOT NULL,
    counter BIGINT NOT NULL,
    cashback_storage NUMERIC NOT NULL,
    reached_max_cashback_rate BOOLEAN NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX user_email_index ON user_account (email);

CREATE UNIQUE INDEX user_phone_index ON user_account (phone);