package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.film.ResponseFilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public final class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @GetMapping
    public Collection<ResponseUserDto> findAll() {
        log.info("Handling GET /users request.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseUserDto findById(@PathVariable Long id) {
        log.info("Handling GET /users/{}.", id);
        return userService.findById(id);
    }

    @GetMapping("/{id}/feed")
    public Collection<EventDto> getFeed(@PathVariable Long id) {
        log.info("Handling GET /users/{}/feed.", id);
        return userService.getFeed(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDto create(@Valid @RequestBody NewUserDto dto) {
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

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Handling DELETE /users/{}.", userId);
        userService.delete(userId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<ResponseFilmDto> recommend(@PathVariable Long id) {
        log.info("Handling GET /users/{}/recommendations.", id);
        return filmService.recommend(id);
    }
}
