package ru.yandex.practicum.filmorate.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

@UtilityClass
public class ReviewMapper {

    public static Review toEntity(NewReviewDto request) {
        if (request == null) {
            return null;
        }
        return Review.builder()
                .content(request.getContent())
                .isPositive(request.getIsPositive())
                .userId(request.getUserId())
                .filmId(request.getFilmId())
                .useful(0)
                .build();
    }

    public static ResponseReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }
        return ResponseReviewDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .useful(review.getUseful())
                .build();
    }

    public static void updateEntity(UpdateReviewDto dto, Review existingReview) {
        if (dto == null || existingReview == null) {
            return;
        }
        existingReview.setContent(dto.getContent());
        existingReview.setIsPositive(dto.getIsPositive());
    }
}

