package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.ResponseUserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/friends")
public class FriendsController {

    private final UserService userService;

    @GetMapping
    public List<ResponseUserDto> getFriends(@PathVariable Long userId) {
        log.info("Handling GET /users/{}/friends request.", userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/common/{userId2}")
    public List<ResponseUserDto> getCommonFriends(@PathVariable Long userId, @PathVariable Long userId2) {
        log.info("Handling GET /users/{}/friends/common/{} request.", userId, userId2);
        return userService.getCommonFriends(userId, userId2);
    }

    @PutMapping("/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Handling PUT /users/{}/friends/{} request.", userId, friendId);
        userService.addFriend(userId, friendId);
        log.trace("PUT /users/{}/friends/{} successful.", userId, friendId);
    }

    @DeleteMapping("/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Handling DELETE /users/{}/friends/{} request.", userId, friendId);
        userService.deleteFriend(userId, friendId);
        log.trace("DELETE /users/{}/friends/{} successful.", userId, friendId);
    }



}
