package ru.yandex.practicum.filmorate.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ResponseReviewDto {
    Long reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Integer useful;
}
