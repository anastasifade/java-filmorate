package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.exceptions.DuplicateDataException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserControllerTests {

    private UserController controller;

    @BeforeEach
    void createUserController() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void getUsers_returnsEmptyWhenNoUsers() {
        List<ResponseUserDto> emptyList = new ArrayList<>();
        Assertions.assertEquals(emptyList, controller.findAll());
    }

    @Test
    void getUsers_returnsUserDto() {
        CreateUserDto user1 = new CreateUserDto("email@email.email", "a", "a", LocalDate.now());
        CreateUserDto user2 = new CreateUserDto("mail@mail.mail", "b", "b", LocalDate.now());

        List<ResponseUserDto> expectedList = List.of(controller.create(user1), controller.create(user2));
        Assertions.assertEquals(expectedList, controller.findAll());
    }

    @Test
    void postUser_returnsResponseUserDto_withId() {
        CreateUserDto user = new CreateUserDto("email@email.email", "a", "a", LocalDate.now());

        ResponseUserDto dto = controller.create(user);
        Assertions.assertNotNull(dto.getId());
    }

    @Test
    void postUser_setsNameToLogin_ifNoNameSubmitted() {
        String login = "login";
        CreateUserDto user = new CreateUserDto("email@email.email", login, "", LocalDate.now());
        ResponseUserDto dto = controller.create(user);
        Assertions.assertEquals(login, dto.getName());
    }

    @Test
    void postUser_failsWhenLoginTaken() {
        String login = "login";
        CreateUserDto user1 = new CreateUserDto("email@email.email", login, "a", LocalDate.now());
        CreateUserDto user2 = new CreateUserDto("mail@mail.mail", login, "b", LocalDate.now());

        controller.create(user1);
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.create(user2));
    }

    @Test
    void postUser_failsWhenEmailTaken() {
        String email = "email@email.email";
        CreateUserDto user1 = new CreateUserDto(email, "a", "a", LocalDate.now());
        CreateUserDto user2 = new CreateUserDto(email, "b", "b", LocalDate.now());

        controller.create(user1);
        Assertions.assertThrows(DuplicateDataException.class, () -> controller.create(user2));
    }

    @Test
    void putRequest_updatesField() {
        CreateUserDto createDto = new CreateUserDto("email@email.email", "a", "a", LocalDate.now());
        ResponseUserDto user = controller.create(createDto);

        String newName = "name";
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(user.getId());
        updateDto.setName(newName);

        ResponseUserDto updatedUser = controller.update(updateDto);

        Assertions.assertEquals(newName, updatedUser.getName());
    }

    @Test
    void putRequest_failsWhenUpdatedEmailIsOccupied() {
        String email = "email@email.email";
        CreateUserDto user1Dto = new CreateUserDto(email, "a", "a", LocalDate.now());
        controller.create(user1Dto);
        CreateUserDto user2Dto = new CreateUserDto("different@email.com", "b", "b", LocalDate.now());

        ResponseUserDto user2 = controller.create(user2Dto);

        UpdateUserDto updateUser2Dto = new UpdateUserDto();
        updateUser2Dto.setId(user2.getId());
        updateUser2Dto.setEmail(email);

        Assertions.assertThrows(DuplicateDataException.class, () -> controller.update(updateUser2Dto));
    }
}
