package ru.yandex.practicum.filmorate.exceptions;

public class FailedToDeleteException extends RuntimeException {
    public FailedToDeleteException(String message) {
        super(message);
    }
}
