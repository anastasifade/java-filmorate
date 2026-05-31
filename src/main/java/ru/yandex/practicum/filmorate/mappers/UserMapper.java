package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@UtilityClass
public class UserMapper {

    public static User toUser(NewUserDto dto) {
        String login = dto.getLogin().trim();
        String name = (dto.getName() == null || dto.getName().isBlank()) ? login : dto.getName().trim();
        String email = dto.getEmail().trim();

        return User.builder()
                .login(login)
                .email(email)
                .name(name)
                .birthday(dto.getBirthday())
                .build();
    }

    public static User toUser(UpdateUserDto dto, User user) {
        String login = (dto.getLogin() == null) ? user.getLogin() : dto.getLogin().trim();
        String name = (dto.getName() == null) ? user.getName() : dto.getName().trim();
        String email = (dto.getEmail() == null) ? user.getEmail() : dto.getEmail().trim();
        LocalDate birthday = (dto.getBirthday() == null) ? user.getBirthday() : dto.getBirthday();

        return User.builder()
                .id(user.getId())
                .login(login)
                .email(email)
                .name(name)
                .birthday(birthday)
                .build();
    }

    public static ResponseUserDto toDto(User user) {
        return ResponseUserDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

}
