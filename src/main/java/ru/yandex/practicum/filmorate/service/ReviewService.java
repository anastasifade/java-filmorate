package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Collection<Review> findByFilmId(Long filmId, int count) {
        return reviewStorage.findByFilmId(filmId, count);
    }

    public Review create(Review review) {
        userService.findById(review.getUserId());
        filmService.findById(review.getFilmId());

        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void putLike(Long id, Long userId) {
        findById(id);
        userService.findById(userId);
        reviewStorage.putLikeOrDislike(id, userId, true);
    }

    public void putDislike(Long id, Long userId) {
        findById(id);
        userService.findById(userId);
        reviewStorage.putLikeOrDislike(id, userId, false);
    }

    public Review findById(Long id) {
        return reviewStorage.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Review with id [%d] not found.", id)));
    }

    public void delete(Long id) {
        findById(id);
        reviewStorage.delete(id);
    }
}
