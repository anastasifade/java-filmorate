package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.InMemoryStorage;

import java.util.List;
import java.util.Set;

@Component
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public boolean isLoginOccupied(String login) {
        return storage.values()
                .stream()
                .anyMatch(user -> user.getLogin().equalsIgnoreCase(login));
    }

    @Override
    public boolean isEmailOccupied(String email) {
        return storage.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public List<User> getFriends(Long userId) {
        return storage.get(userId)
                .getFriends()
                .stream()
                .filter(storage::containsKey)
                .map(storage::get)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long firstId, Long secondId) {
        Set<Long> firstUserFriends = storage.get(firstId).getFriends();
        return storage.get(secondId)
                .getFriends()
                .stream()
                .filter(id -> firstUserFriends.contains(id) &&
                        storage.containsKey(id))
                .map(storage::get)
                .toList();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        storage.get(userId).getFriends().add(friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        storage.get(userId).getFriends().remove(friendId);
    }
}
