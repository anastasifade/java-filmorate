package ru.yandex.practicum.filmorate.dto;

import lombok.Value;

/**
 *  DTO for simple entities that only require to transfer one field - id
 *  Example - MPA, Genre
*/
@Value
public class Id {
    private Long id;
}
