package ru.yandex.practicum.filmorate.exceptions;

public final class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
