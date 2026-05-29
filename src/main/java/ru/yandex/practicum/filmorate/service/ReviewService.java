package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.event.EventDbStorage;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
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
    private final EventDbStorage eventStorage;

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

        eventStorage.create(EventMapper.newEvent(createdReview.getUserId(), createdReview.getFilmId(),
                EventType.REVIEW, EventOperation.ADD));

        return ReviewMapper.toDto(createdReview);
    }

    public ResponseReviewDto update(UpdateReviewDto dto) {
        Review oldReview = getReviewEntityById(dto.getReviewId());

        ReviewMapper.updateEntity(dto, oldReview);

        Review updatedReview = reviewStorage.update(oldReview);

        eventStorage.create(EventMapper.newEvent(updatedReview.getUserId(), updatedReview.getFilmId(),
                EventType.REVIEW, EventOperation.UPDATE));

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
        Review rev = getReviewEntityById(id);
        reviewStorage.delete(id);

        eventStorage.create(EventMapper.newEvent(rev.getUserId(), rev.getFilmId(),
                EventType.REVIEW, EventOperation.REMOVE));
    }

    public void deleteLike(Long id, Long userId) {
        validateReviewAndUser(id, userId);
        boolean deleted = reviewStorage.deleteLike(id, userId);

        if (!deleted) {
            throw new NotFoundException(String.format("Like from user [%d] on review [%d] not found.", userId, id));
        }

        log.info("Successfully deleted like from user {} on review {}.", userId, id);
    }

    public void deleteDislike(Long id, Long userId) {
        validateReviewAndUser(id, userId);
        boolean deleted = reviewStorage.deleteDislike(id, userId);

        if (!deleted) {
            throw new NotFoundException(String.format("Dislike from user [%d] on review [%d] not found.", userId, id));
        }

        log.info("Successfully deleted dislike from user {} on review {}.", userId, id);
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
