package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public final class Review implements Entity {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;
    private Long filmId;
    private Integer useful;

    @Override
    public Long getId() {
        return reviewId;
    }

    @Override
    public void setId(Long id) {
        this.reviewId = id;
    }
}
