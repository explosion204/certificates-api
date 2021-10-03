INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('test1', '1description1', 1, 1, '2021-09-25 00:00:00', '2021-09-25 00:00:00');

INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('hello there', 'general kenobi', 1.1, 2, '2021-09-25 00:00:00', '2021-09-25 00:00:00');

INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('aaaaa', 'lorem ipsum', 10.3, 10, '2021-09-25 00:00:00', '2021-09-25 00:00:00');

INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
VALUES ('test1', 'description', 1.0, 20, '2021-09-25 00:00:00', '2021-09-25 00:00:00');

INSERT INTO tag (name)
VALUES ('tag1');

INSERT INTO tag (name)
VALUES ('tag2');

INSERT INTO tag (name)
VALUES ('tag3');

INSERT INTO certificate_tag (id_certificate, id_tag)
VALUES (1, 1);