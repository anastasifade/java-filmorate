package ru.yandex.practicum.filmorate.exceptions;

public final class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message) {
        super(message);
    }
}
