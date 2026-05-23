package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Collection<ResponseReviewDto> findByFilmId(Long filmId, int count) {
        return reviewStorage.findByFilmId(filmId, count).stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public ResponseReviewDto create(NewReviewDto dto) {
        userService.findById(dto.getUserId());
        filmService.findById(dto.getFilmId());

        Review review = ReviewMapper.toEntity(dto);
        Review createdReview = reviewStorage.create(review);

        return ReviewMapper.toDto(createdReview);
    }

    public ResponseReviewDto update(UpdateReviewDto dto) {
        Review oldReview = getReviewEntityById(dto.getReviewId());

        ReviewMapper.updateEntity(dto, oldReview);

        Review updatedReview = reviewStorage.update(oldReview);
        return ReviewMapper.toDto(updatedReview);
    }

    public void putLike(Long id, Long userId) {
        validateReviewAndUser(id, userId);
        reviewStorage.putLikeOrDislike(id, userId, true);
    }

    public void putDislike(Long id, Long userId) {
        validateReviewAndUser(id, userId);
        reviewStorage.putLikeOrDislike(id, userId, false);
    }

    public ResponseReviewDto findById(Long id) {
        return ReviewMapper.toDto(getReviewEntityById(id));
    }

    public void delete(Long id) {
        getReviewEntityById(id);
        reviewStorage.delete(id);
    }

    public void deleteLike(Long id, Long userId) {
        validateReviewAndUser(id, userId);
        reviewStorage.deleteLikeOrDislike(id, userId);
    }

    public void deleteDislike(Long id, Long userId) {
        validateReviewAndUser(id, userId);
        reviewStorage.deleteLikeOrDislike(id, userId);
    }

    private Review getReviewEntityById(Long id) {
        return reviewStorage.findById(id).orElseThrow(() -> {
            log.warn("Review with id={} not found.", id);
            return new NotFoundException(String.format("Review with id [%d] not found.", id));
        });
    }

    private void validateReviewAndUser(Long reviewId, Long userId) {
        getReviewEntityById(reviewId);
        userService.findById(userId);
    }
}
