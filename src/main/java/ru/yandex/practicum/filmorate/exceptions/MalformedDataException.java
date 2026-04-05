package ru.yandex.practicum.filmorate.exceptions;

public class MalformedDataException extends RuntimeException {
    public MalformedDataException(String message) {
        super(message);
    }
}
