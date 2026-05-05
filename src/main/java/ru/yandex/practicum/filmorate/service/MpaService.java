package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final Storage<MPA> storage;

    public Collection<MPA> findAll() {
        log.trace("GET /mpa request received by MpaService.");
        return storage.findAll();
    }

    public MPA findById(Long id) {
        log.trace("GET /mpa/{} request received by MpaService.", id);
        return storage.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("MPA {id=%d} not found.", id)));
    }

}
