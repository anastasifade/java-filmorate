package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.ResponseDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/directors")
@RestController
@Slf4j
public final class DirectorController {
    private final DirectorService service;

    @GetMapping
    public Collection<ResponseDirectorDto> findAll() {
        log.info("Handling GET /directors request.");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseDirectorDto findById(@PathVariable Long id) {
        log.info("Handling GET /directors/{} request.", id);
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDirectorDto create(@Valid @RequestBody NewDirectorDto dto) {
        log.info("Handling POST /directors request.");
        log.debug("POST request to create object: {}.", dto);

        return service.create(dto);
    }

    @PutMapping
    public ResponseDirectorDto update(@Valid @RequestBody UpdateDirectorDto dto) {
        log.info("Handling PUT /directors request.");
        log.debug("PUT request to update object to: {}.", dto);

        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Handling DELETE /directors request.");
        log.debug("Request to delete director with id={}.", id);

        service.delete(id);
    }
}
