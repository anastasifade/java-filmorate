package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.ResponseDirectorDto;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage storage;

    public Collection<ResponseDirectorDto> findAll() {
        log.trace("GET /directors request received by DirectorService.");
        return storage.findAll()
                .stream()
                .map(DirectorMapper::toDto)
                .toList();
    }

    public ResponseDirectorDto findById(Long id) {
        log.trace("GET /directors/{} request received by DirectorService.", id);
        Director director = storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Director with id={%s} not found.", id)));

        return DirectorMapper.toDto(director);
    }

    public ResponseDirectorDto create(NewDirectorDto dto) {
        log.trace("PUT /directors request received by DirectorService.");
        Director dir = storage.create(DirectorMapper.toDirectorFromDto(dto));
        return DirectorMapper.toDto(dir);
    }

    public ResponseDirectorDto update(UpdateDirectorDto dto) {
        log.trace("PUT /directors/{} request received by DirectorService.", dto.getId());
        Director director = storage.findById(dto.getId())
                .orElseThrow(() ->
                        new NotFoundException(String.format("Director with id={%s} not found.", dto.getId())));

        director = storage.update(DirectorMapper.toDirectorFromDto(dto, director));
        return DirectorMapper.toDto(director);
    }

    public void delete(Long id) {
        log.trace("DELETE /directors/{} request received by DirectorService.", id);
        findById(id); // throws NotFoundException if director id does not exist
        storage.delete(id);
    }
}
