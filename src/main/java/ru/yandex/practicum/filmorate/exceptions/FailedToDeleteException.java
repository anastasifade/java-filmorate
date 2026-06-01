package ru.yandex.practicum.filmorate.exceptions;

public final class FailedToDeleteException extends RuntimeException {
    public FailedToDeleteException(String message) {
        super(message);
    }
}
