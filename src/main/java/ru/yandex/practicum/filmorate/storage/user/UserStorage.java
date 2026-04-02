package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;

public interface UserStorage extends Storage<User> {

    // TODO: add search parameter logic
    Collection<User> findAllBy();

    public boolean isLoginOccupied(String login);

    public boolean isEmailOccupied(String email);
}
