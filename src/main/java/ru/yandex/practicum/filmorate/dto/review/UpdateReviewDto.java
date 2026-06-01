package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class UpdateReviewDto {
    @NotNull
    Long reviewId;
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
}
