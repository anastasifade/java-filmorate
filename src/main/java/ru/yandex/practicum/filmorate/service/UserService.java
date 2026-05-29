package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.dal.event.EventDbStorage;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.MalformedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final EventDbStorage eventStorage;

    public List<ResponseUserDto> findAll() {
        log.trace("GET /users request received by UserService.");
        return userStorage.findAll().stream().map(UserMapper::toDto).toList();
    }

    public ResponseUserDto findById(Long id) {
        log.trace("GET /users/{} request received by UserService.", id);
        Optional<User> userOptional = userStorage.findById(id);
        if (userOptional.isEmpty()) {
            throwNotFound(id);
        }
        return UserMapper.toDto(userOptional.get());
    }

    public Collection<EventDto> getFeed(Long id) {
        log.trace("GET /users/{}/feed request received by UserService.", id);
        findById(id); // validating user id
        return eventStorage.getFeed(id)
                .stream()
                .map(EventMapper::toDto)
                .toList();
    }

    public ResponseUserDto create(NewUserDto dto) {
        log.trace("POST /users request received by UserService.");

        String login = dto.getLogin().trim();
        if (userStorage.isLoginOccupied(login)) {
            throwDuplicateLogin(dto.getLogin());
        }

        String email = dto.getEmail().trim();
        if (userStorage.isEmailOccupied(email)) {
            throwDuplicateEmail(email);
        }

        User user = UserMapper.toUser(dto);
        user = userStorage.create(user);
        return UserMapper.toDto(user);
    }

    public ResponseUserDto update(UpdateUserDto dto) {
        log.trace("PUT /users request received by UserService.");
        Optional<User> userOptional = userStorage.findById(dto.getId());
        if (userOptional.isEmpty()) {
            throwNotFound(dto.getId());
        }

        User user = userOptional.get();

        String newLogin = dto.getLogin() == null ? user.getLogin() : dto.getLogin().trim();
        if (!newLogin.equalsIgnoreCase(user.getLogin()) && userStorage.isLoginOccupied(newLogin)) {
            throwDuplicateLogin(newLogin);
        }

        String newEmail = dto.getEmail() == null ? user.getEmail() : dto.getEmail().trim();
        if (!newEmail.equalsIgnoreCase(user.getEmail()) && userStorage.isEmailOccupied(newEmail)) {
            throwDuplicateEmail(newEmail);
        }

        user = UserMapper.toUser(dto, user);
        user = userStorage.update(user);
        return UserMapper.toDto(user);
    }

    public List<ResponseUserDto> getFriends(Long userId) {
        log.trace("GET /users/{}/friends request received by UserService.", userId);
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) {
            throwNotFound(userId);
        }

        return userStorage.getFriends(userId).stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public List<ResponseUserDto> getCommonFriends(Long userId1, Long userId2) {
        log.trace("GET /users/{}/friends/common/{} request received by UserService.", userId1, userId2);

        Optional<User> user1Opt = userStorage.findById(userId1);
        if (user1Opt.isEmpty()) {
            throwNotFound(userId1);
        }

        Optional<User> user2Opt = userStorage.findById(userId2);
        if (user2Opt.isEmpty()) {
            throwNotFound(userId2);
        }

        return userStorage.getCommonFriends(userId1, userId2)
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public void addFriend(Long userId, Long friendId) {
        log.trace("PUT /users/{}/friends/{} request received by UserService.", userId, friendId);

        if (userId.equals(friendId)) {
            log.debug("Friend not added. UserId={} = friendId={}.", userId, friendId);
            throw new MalformedDataException("Cannot add user to their own friends list.");
        }

        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) {
            throwNotFound(userId);
        }

        Optional<User> friendOptional = userStorage.findById(friendId);
        if (friendOptional.isEmpty()) {
            throwNotFound(friendId);
        }

        userStorage.addFriend(userId, friendId);
        eventStorage.create(EventMapper.newEvent(userId, friendId, EventType.FRIEND, EventOperation.ADD));
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.trace("DELETE /users/{}/friends/{} request received by UserService.", userId, friendId);
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) {
            throwNotFound(userId);
        }

        Optional<User> friendOptional = userStorage.findById(friendId);
        if (friendOptional.isEmpty()) {
            throwNotFound(friendId);
        }

        userStorage.deleteFriend(userId, friendId);
        eventStorage.create(EventMapper.newEvent(userId, friendId, EventType.FRIEND, EventOperation.REMOVE));
    }

    private void throwNotFound(Long id) {
        log.warn("User with id={} not found.", id);
        throw new NotFoundException(String.format("User with id [%d] not found.", id));
    }

    private void throwDuplicateLogin(String login) {
        log.warn("Request failed: login={} already occupied.", login);
        throw new DuplicateDataException(String.format("Login [%s] already occupied.", login));
    }

    private void throwDuplicateEmail(String email) {
        log.warn("Request failed: email={} already occupied.", email);
        throw new DuplicateDataException(String.format("Email [%s] already taken.", email));
    }

    public void delete(Long id) {
        log.trace("DELETE /users/{} request received by UserService.", id);

        if (userStorage.findById(id).isEmpty()) {
            throwNotFound(id);
        }

        userStorage.delete(id);
    }

}
