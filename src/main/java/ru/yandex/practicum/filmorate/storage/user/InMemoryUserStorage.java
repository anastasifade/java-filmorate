package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.List;

@Component
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public List<User> findAllBy() {
        return null;
    }

    @Override
    public boolean isLoginOccupied(String login) {
        return storage.values().stream().anyMatch(user -> user.getLogin().equalsIgnoreCase(login));
    }

    @Override
    public boolean isEmailOccupied(String email) {
        return storage.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

}
