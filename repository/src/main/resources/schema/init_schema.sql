USE certificates_db;

CREATE TABLE gift_certificate (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) unsigned NOT NULL,
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

CREATE TABLE certificate_tag (
   id_certificate BIGINT UNSIGNED NOT NULL,
   id_tag BIGINT UNSIGNED NOT NULL,
   CONSTRAINT certificate_fk FOREIGN KEY (id_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE,
   CONSTRAINT tag_fk FOREIGN KEY (id_tag) REFERENCES tag (id) ON DELETE CASCADE
);
