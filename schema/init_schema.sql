USE certificates_db;

CREATE TABLE gift_certificate (
    id BIGINT UNSIGNED NOT NULL UNIQUE AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR (100) NOT NULL,
    price DECIMAL(10, 2) unsigned NOT NULL,
    duration INT UNSIGNED NOT NULL,
    create_date TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE tag (
   id BIGINT unsigned NOT NULL UNIQUE AUTO_INCREMENT,
   name VARCHAR(50) DEFAULT NULL,
   PRIMARY KEY (id)
);

CREATE TABLE certificate_tag (
   id bigint(20) unsigned NOT NULL UNIQUE AUTO_INCREMENT,
   id_certificate bigint(20) unsigned NOT NULL,
   id_tag bigint(20) unsigned NOT NULL,
   PRIMARY KEY (id),
   CONSTRAINT certificate_fk FOREIGN KEY (id_certificate) REFERENCES gift_certificate (id) ON DELETE CASCADE,
   CONSTRAINT tag_fk FOREIGN KEY (id_tag) REFERENCES tag (id) ON DELETE CASCADE
);
