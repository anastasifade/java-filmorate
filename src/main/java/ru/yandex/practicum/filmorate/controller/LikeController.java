package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films/{filmId}/like")
public class LikeController {

    private final FilmService filmService;

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Handling PUT /films/{}/like/{}.", filmId, userId);
        filmService.addLike(filmId, userId);
        log.trace("PUT /films/{}/like/{} request successful.", filmId, userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Handling DELETE /films/{}/like/{}.", filmId, userId);
        filmService.deleteLike(filmId, userId);
        log.trace("DELETE /films/{}/like/{} request successful.", filmId, userId);
    }

}
