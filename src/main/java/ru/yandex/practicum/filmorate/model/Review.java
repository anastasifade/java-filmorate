package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Review implements Entity {
    private Long id;
    private Integer rating;
    @NonNull
    private Boolean isPositive;
    @NonNull
    private Long userId;
    @NonNull
    private Long filmId;
    @NotBlank
    private String context;
}
