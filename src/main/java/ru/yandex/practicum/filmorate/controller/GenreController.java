package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
public final class GenreController {

    private final GenreService service;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Handling GET /genres request.");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable Long id) {
        log.info("Handling GET /genres/{} request.", id);
        return service.findById(id);
    }

}
