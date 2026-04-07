package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<ResponseUserDto> findAll() {
        log.trace("GET /users request received by UserService.");
        return userStorage.findAll().stream().map(this::toResponseDto).toList();
    }

    public ResponseUserDto findById(Long id) {
        log.trace("GET /users/{} request received by UserService.", id);
        Optional<User> userOptional = userStorage.findById(id);
        if (userOptional.isEmpty()) {
            throwNotFound(id);
        }
        return toResponseDto(userOptional.get());
    }

    public ResponseUserDto create(CreateUserDto dto) {
        log.trace("POST /users request received by UserService.");
        String login = dto.getLogin().trim();
        String email = dto.getEmail().trim();
        String name = (dto.getName() == null) ? login : dto.getName().trim();
        LocalDate birthday = dto.getBirthday();

        validateLogin(login);
        validateEmail(email);

        User user = User.builder()
                .login(login)
                .email(email)
                .name(name)
                .friends(new HashSet<>())
                .build();

        if (birthday != null) {
            user.setBirthday(birthday);
        }

        user = userStorage.create(user);
        return toResponseDto(user);
    }

    public ResponseUserDto update(UpdateUserDto dto) {
        log.trace("PUT /users request received by UserService.");
        Optional<User> userOptional = userStorage.findById(dto.getId());
        if (userOptional.isEmpty()) {
            throwNotFound(dto.getId());
        }

        User user = userOptional.get();

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            String email = dto.getEmail().trim();
            user.setEmail(email);
        }

        if (dto.getLogin() != null && !dto.getLogin().equalsIgnoreCase(user.getLogin())) {
            String login = dto.getLogin().trim();
            validateLogin(login);
            user.setLogin(login);
        }

        if (dto.getName() != null) {
            user.setName(dto.getName().trim());
            log.debug("Updated field [name]: {}.", user.getName());
        }

        if (dto.getBirthday() != null) {
            LocalDate birthday = dto.getBirthday();
            user.setBirthday(birthday);
            log.debug("Updated field [birthday]: {}.", birthday);
        }

        user = userStorage.update(user);
        return toResponseDto(user);
    }

    public List<ResponseUserDto> getFriends(Long userId) {
        log.trace("GET /users/{}/friends request received by UserService.", userId);
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) {
            throwNotFound(userId);
        }

        return userStorage.getFriends(userId).stream()
                .map(this::toResponseDto)
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
                .map(this::toResponseDto)
                .toList();
    }

    public void addFriend(Long userId, Long friendId) {
        log.trace("PUT /users/{}/friends/{} request received by UserService.", userId, friendId);
        Optional<User> userOptional = userStorage.findById(userId);
        if (userOptional.isEmpty()) {
            throwNotFound(userId);
        }

        Optional<User> friendOptional = userStorage.findById(friendId);
        if (friendOptional.isEmpty()) {
            throwNotFound(friendId);
        }

        User user = userOptional.get();
        if (user.getFriends().contains(friendId)) {
            log.debug("Attempt to add user id={} to friends by user id={}: users are already friends. No changes done.",
                    friendId, userId);
            return;
        }

        userStorage.addFriend(userId, friendId);
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

        User user = userOptional.get();
        if (!user.getFriends().contains(friendId)) {
            log.debug("Attempt to delete user id={} from friends by user id={}: user not found in friends list. " +
                            "No changes done.",
                    friendId, userId);
            return;
        }

        userStorage.deleteFriend(userId, friendId);
    }

    private void validateLogin(String login) {
        if (userStorage.isLoginOccupied(login)) {
            log.warn("Request failed: login already occupied.");
            throw new DuplicateDataException("Login already occupied.");
        }
    }

    private void validateEmail(String email) {
        if (userStorage.isEmailOccupied(email)) {
            log.warn("Request failed: email already occupied.");
            throw new DuplicateDataException("Email already taken.");
        }
    }

    private ResponseUserDto toResponseDto(User user) {
        return ResponseUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .birthday(user.getBirthday())
                .login(user.getLogin())
                .email(user.getEmail())
                .build();
    }

    private void throwNotFound(Long id) {
        log.warn("User with id={} not found.", id);
        throw new NotFoundException(String.format("User with id [%d] not found.", id));
    }

}
