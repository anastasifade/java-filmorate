package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.Set;

@Component
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public boolean isLoginOccupied(String login) {
        return storage.values().stream().anyMatch(user -> user.getLogin().equalsIgnoreCase(login));
    }

    @Override
    public boolean isEmailOccupied(String email) {
        return storage.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        return storage.get(userId).getFriends();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        storage.get(userId).getFriends().add(friendId);
        storage.get(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        storage.get(userId).getFriends().remove(friendId);
        storage.get(friendId).getFriends().remove(userId);
    }
}
