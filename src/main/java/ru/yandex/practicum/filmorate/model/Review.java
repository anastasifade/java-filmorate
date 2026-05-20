package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Review {
    private Long reviewId;
    @NotBlank
    private String content;
    @NonNull
    private Boolean isPositive;
    @NonNull
    private Long userId;
    @NonNull
    private Long filmId;
    private Integer useful;
}
