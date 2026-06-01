package ru.yandex.practicum.filmorate.dto.review;

import lombok.*;

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
