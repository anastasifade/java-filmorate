package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mpa")
@RestController
public final class MpaController {
    private final MpaService service;

    @GetMapping
    public Collection<MPA> findAll() {
        log.info("Handling GET /mpa request.");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MPA findById(@PathVariable Long id) {
        log.info("Handling GET /mpa/{} request.", id);
        return service.findById(id);
    }
}
