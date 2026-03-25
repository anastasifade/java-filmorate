package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;

import java.util.ArrayList;
import java.util.List;

public class UserControllerTests {

    private UserController controller;

    @BeforeEach
    void createUserController() {
        controller = new UserController();
    }

    @Test
    void getUsers_returnsEmptyWhenNoUsers() {
        List<ResponseUserDto> emptyList = new ArrayList<>();
        Assertions.assertEquals(emptyList, controller.findAll());
    }

    @Test
    void getUsers_returnsUserDto() {
        CreateUserDto user1 = CreateUserDto.builder()
                .name("a")
                .email("email@email.email")
                .login("a")
                .build();
        CreateUserDto user2 = CreateUserDto.builder()
                .name("b")
                .email("mail@mail.mail")
                .login("b")
                .build();

        List<ResponseUserDto> expectedList = List.of(controller.create(user1), controller.create(user2));
        Assertions.assertEquals(expectedList, controller.findAll());
    }

    @Test
    void postUser_returnsResponseUserDto_withId() {
        CreateUserDto user = CreateUserDto.builder()
                .name("a")
                .email("email@email.email")
                .login("a")
                .build();

        ResponseUserDto dto = controller.create(user);
        Assertions.assertNotNull(dto.getId());
    }

    @Test
    void postUser_setsNameToLogin_ifNoNameSubmitted() {
        String login = "login";
        CreateUserDto user = CreateUserDto.builder()
                .email("email@email.email")
                .login(login)
                .build();
        ResponseUserDto dto = controller.create(user);
        Assertions.assertEquals(login, dto.getName());
    }

    @Test
    void postUser_failsWhenLoginTaken() {
        String login = "login";
        CreateUserDto user1 = CreateUserDto.builder()
                .name("a")
                .email("email@email.email")
                .login(login)
                .build();
        CreateUserDto user2 = CreateUserDto.builder()
                .name("b")
                .email("mail@mail.mail")
                .login(login)
                .build();

        controller.create(user1);
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.create(user2));
    }

    @Test
    void postUser_failsWhenEmailTaken() {
        String email = "email@email.email";
        CreateUserDto user1 = CreateUserDto.builder()
                .name("a")
                .email(email)
                .login("a")
                .build();
        CreateUserDto user2 = CreateUserDto.builder()
                .name("b")
                .email(email)
                .login("b")
                .build();

        controller.create(user1);
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.create(user2));
    }

    @Test
    void putRequest_updatesField() {
        CreateUserDto createDto = CreateUserDto.builder()
                .name("a")
                .email("email@email.email")
                .login("a")
                .build();
        ResponseUserDto user = controller.create(createDto);

        String newName = "name";
        UpdateUserDto updateDto = UpdateUserDto.builder().id(user.getId()).name(newName).build();
        ResponseUserDto updatedUser = controller.update(updateDto);

        Assertions.assertEquals(newName, updatedUser.getName());
    }

    @Test
    void putRequest_failsWhenUpdatedEmailIsOccupied() {
        String email = "email@email.email";
        CreateUserDto user1Dto = CreateUserDto.builder()
                .name("a")
                .email(email)
                .login("a")
                .build();
        controller.create(user1Dto);
        CreateUserDto user2Dto = CreateUserDto.builder()
                .name("b")
                .email("different@email.com")
                .login("b")
                .build();

        ResponseUserDto user2 = controller.create(user2Dto);

        UpdateUserDto updateUser2Dto = UpdateUserDto.builder().id(user2.getId()).email(email).build();
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.update(updateUser2Dto));
    }
}
