package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {

    public boolean isLoginOccupied(String login);

    public boolean isEmailOccupied(String email);

    public List<User> getFriends(Long userId);

    public List<User> getCommonFriends(Long user1, Long user2);

    public void addFriend(Long userId, Long friendId);

    public void deleteFriend(Long userId, Long friendId);
}
