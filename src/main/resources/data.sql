MERGE INTO genres (name) KEY(name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

MERGE INTO mpa (name) KEY(name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

MERGE INTO event_types (name) KEY(name)
VALUES ('LIKE'),
       ('REVIEW'),
       ('FRIEND');

MERGE INTO operations (name) KEY(name)
VALUES ('ADD'),
       ('UPDATE'),
       ('REMOVE');