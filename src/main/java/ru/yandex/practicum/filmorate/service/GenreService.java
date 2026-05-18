package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final Storage<Genre> storage;

    public Collection<Genre> findAll() {
        log.trace("GET /genres request received by GenreService.");
        return storage.findAll();
    }

    public Genre findById(Long id) {
        log.trace("GET /genres/{} request received by GenreService.", id);
        return storage.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Genre {id=%d} not found.", id)));
    }

}
