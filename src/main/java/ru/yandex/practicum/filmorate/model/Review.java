package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"reviewId"})
@Builder
public class Review implements Entity {
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

    @Override
    public Long getId() {
        return reviewId;
    }

    @Override
    public void setId(Long id) {
        this.reviewId = id;
    }
}
