package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Value
@Builder
@AllArgsConstructor
public class NewReviewDto {
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
}
