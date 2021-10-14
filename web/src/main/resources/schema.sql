CREATE TABLE gift_certificate (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) UNSIGNED NOT NULL,
    duration INT UNSIGNED NOT NULL,
    create_date TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE tag (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    name VARCHAR(50) UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE app_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    name VARCHAR(100) UNIQUE
);

CREATE TABLE app_order (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    cost DECIMAL(10, 2) UNSIGNED NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    id_user BIGINT UNSIGNED NOT NULL,

    CONSTRAINT user_fk FOREIGN KEY (id_user) REFERENCES app_user (id)
);

CREATE TABLE certificate_tag (
    id_certificate BIGINT UNSIGNED NOT NULL,
    id_tag BIGINT UNSIGNED NOT NULL,

    CONSTRAINT ct_certificate_fk FOREIGN KEY (id_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE,
    CONSTRAINT ct_tag_fk FOREIGN KEY (id_tag) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE certificate_order (
    id_certificate BIGINT UNSIGNED NOT NULL,
    id_order BIGINT UNSIGNED NOT NULL,

    CONSTRAINT co_certificate_fk FOREIGN KEY (id_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE,
    CONSTRAINT co_order_fk FOREIGN KEY (id_order) REFERENCES app_order (id)
);

CREATE TABLE audit_table (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    operation VARCHAR(20),
    entity_name VARCHAR(50),
    timestamp TIMESTAMP
);