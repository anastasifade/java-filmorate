package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.exceptions.MalformedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<ResponseUserDto> findAll() {
        return users.values()
                .stream()
                .map(user -> toResponseDto(user))
                .toList();
    }

    @PostMapping
    public ResponseUserDto create(@Valid @RequestBody CreateUserDto userDto) {
        String login = userDto.getLogin().trim().toLowerCase();
        String email = userDto.getEmail().trim().toLowerCase();
        String name = (userDto.getName() == null || userDto.getName().isBlank()) ? login : userDto.getName().trim();
        LocalDate birthday = userDto.getBirthday();

        validateLogin(login);
        validateEmail(email);
        if (birthday != null) {
            validateBirthday(birthday);
        }

        User user = User.builder()
                .id(getNextId())
                .login(login)
                .email(email)
                .name(name)
                .birthday(birthday)
                .build();

        users.put(user.getId(), user);
        return toResponseDto(user);
    }

    @PutMapping
    public ResponseUserDto update(@Valid @RequestBody UpdateUserDto userDto) {

        if (!users.containsKey(userDto.getId())) {
            throw new NotFoundException(String.format("User with id [%s] not found.", userDto.getId()));
        }

        User user = users.get(userDto.getId());

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            String email = userDto.getEmail().trim().toLowerCase();
            validateEmail(email);
            user.setEmail(email);
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName().trim());
        }

        if (userDto.getBirthday() != null) {
            LocalDate birthday = userDto.getBirthday();
            validateBirthday(birthday);
            user.setBirthday(birthday);
        }

        return toResponseDto(user);
    }

    private Long getNextId() {
        Long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    private ResponseUserDto toResponseDto(User user) {
        return ResponseUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }


    /**
     * VALIDATION
     */

    private void validateLogin(String login) {
        boolean isUnique = users.values().stream().noneMatch(user -> user.getLogin().equals(login));
        if (!isUnique) {
            throw new DuplicateDataException("Login already taken.");
        }
    }

    private void validateEmail(String email) {
        boolean isUnique = users.values().stream().noneMatch(user -> user.getEmail().equals(email));
        if (!isUnique) {
            throw new DuplicateDataException("Email already taken.");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new MalformedDataException("Date of birth cannot be in the future.");
        }
    }

}
