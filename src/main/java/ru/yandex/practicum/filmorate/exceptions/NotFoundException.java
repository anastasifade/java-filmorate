package ru.yandex.practicum.filmorate.exceptions;

public final class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
