package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.CreateUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<ResponseUserDto> findAll() {
        log.info("Handling GET /users request.");
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDto create(@Valid @RequestBody CreateUserDto dto) {
        log.info("Handling POST /users request.");
        log.debug("POST request to create object: {}.", dto);

        ResponseUserDto responseDto = userService.create(dto);
        log.info("Created object: {}.", responseDto);
        return responseDto;
    }

    @PutMapping
    public ResponseUserDto update(@Valid @RequestBody UpdateUserDto dto) {
        log.info("Handling PUT /users request.");
        log.debug("PUT /users request for: {}.", dto);

        ResponseUserDto responseDto = userService.update(dto);
        log.info("Updated object: {}.", responseDto);
        return responseDto;
    }

}
