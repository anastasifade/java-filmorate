package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ResponseReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public Collection<ResponseReviewDto> findByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        log.info("Handling GET /reviews request.");
        return reviewService.findByFilmId(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseReviewDto create(@Valid @RequestBody NewReviewDto review) {
        log.info("Handling POST /reviews request.");
        return reviewService.create(review);
    }

    @PutMapping
    public ResponseReviewDto update(@Valid @RequestBody UpdateReviewDto review) {
        log.info("Handling PUT /reviews request.");
        return reviewService.update(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Handling PUT /reviews/{}/like/{}.", id, userId);
        reviewService.putLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Handling PUT /reviews/{}/dislike/{}.", id, userId);
        reviewService.putDislike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Handling DELETE /reviews/{}.", id);
        reviewService.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Handling DELETE /reviews/{}/like/{}.", id, userId);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Handling DELETE /reviews/{}/dislike/{}.", id, userId);
        reviewService.deleteDislike(id, userId);
    }

    @GetMapping("/{id}")
    public ResponseReviewDto findById(@PathVariable Long id) {
        log.info("Handling GET /reviews/{}.", id);
        return reviewService.findById(id);
    }
}
