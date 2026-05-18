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

INSERT INTO users (login, email, name, birthday)
VALUES ('login', 'e@email.com', 'name', '1980-01-01');

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('title', 'description', '2014-01-05', 125, 1);

INSERT INTO films_genres (film_id, genre_id)
VALUES (1, 1);
