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
            log.info("User with id={} not found.", dto.getId());
            throw new NotFoundException(String.format("User with id [%d] not found.", dto.getId()));
        }

        User user = userOptional.get();

        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim();
            validateEmail(email);
            user.setEmail(email);
            log.debug("Updated field [email]: {}.", email);
        }

        if (dto.getLogin() != null) {
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

    private void validateLogin(String login) {
        if (userStorage.isLoginOccupied(login)) {
            log.info("Request failed: login already occupied.");
            throw new DuplicateDataException("Login already occupied.");
        }
    }

    private void validateEmail(String email) {
        if (userStorage.isEmailOccupied(email)) {
            log.info("Request failed: email already occupied.");
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

}
