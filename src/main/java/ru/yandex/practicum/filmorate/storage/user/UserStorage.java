package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.Set;

public interface UserStorage extends Storage<User> {

    // TODO: add search parameter logic
    Collection<User> findAllBy();

    public boolean isLoginOccupied(String login);

    public boolean isEmailOccupied(String email);

    public Set<Long> getFriends(Long userId);

    public void addFriend(Long userId, Long friendId);

    public void deleteFriend(Long userId, Long friendId);
}
