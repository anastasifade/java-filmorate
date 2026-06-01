package ru.yandex.practicum.filmorate.dal.user;

import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage extends Storage<User> {
    public boolean isLoginOccupied(String login);

    public boolean isEmailOccupied(String email);

    public Collection<User> getFriends(Long userId);

    public Collection<User> getCommonFriends(Long user1, Long user2);

    public void addFriend(Long userId, Long friendId);

    public void deleteFriend(Long userId, Long friendId);
}
