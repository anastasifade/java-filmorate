package ru.yandex.practicum.filmorate.dal.review;

import ru.yandex.practicum.filmorate.dal.Storage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage extends Storage<Review> {
    Collection<Review> findByFilmId(Long filmId, int count);

    void putLikeOrDislike(Long reviewId, Long userId, boolean isLike);

    void deleteLikeOrDislike(Long reviewId, Long userId);
}
