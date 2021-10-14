CREATE TABLE gift_certificate (
    id BIGSERIAL PRIMARY KEY, -- the same as BIGINT
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    duration INT NOT NULL,
    create_date TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP NOT NULL
);

CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE
);

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) UNIQUE
    password VARCHAR(60)
);

CREATE TABLE app_order (
    id BIGSERIAL PRIMARY KEY,
    cost DECIMAL(10, 2) NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    id_user BIGINT NOT NULL,

    CONSTRAINT user_fk FOREIGN KEY (id_user) REFERENCES app_user (id)
);

CREATE TABLE certificate_tag (
    id_certificate BIGINT NOT NULL,
    id_tag BIGINT NOT NULL,

    CONSTRAINT ct_certificate_fk FOREIGN KEY (id_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE,
    CONSTRAINT ct_tag_fk FOREIGN KEY (id_tag) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE certificate_order (
    id_certificate BIGINT NOT NULL,
    id_order BIGINT NOT NULL,

    CONSTRAINT co_certificate_fk FOREIGN KEY (id_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE,
    CONSTRAINT co_order_fk FOREIGN KEY (id_order) REFERENCES app_order (id)
);

CREATE TABLE audit_table (
    id BIGSERIAL PRIMARY KEY,
    operation VARCHAR(20),
    entity_name VARCHAR(50),
    timestamp TIMESTAMP
);