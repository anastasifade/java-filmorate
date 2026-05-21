package ru.yandex.practicum.filmorate.dal.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Slf4j
@Repository
@Primary
public class DbReviewStorage extends DbStorage<Review> implements ReviewStorage {
    private static final String CREATE_REVIEW = "INSERT INTO reviews " +
            "(content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, 0)";
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
    private static final String TAKE_REVIEWS_LIST = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String TAKE_REVIEW_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = ? " +
            "ORDER BY useful DESC LIMIT ?";
    private static final String UPDATE_REACTION = "UPDATE reviews SET useful = useful + ? WHERE id = ?";

    public DbReviewStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super("reviews", jdbc, mapper);
    }

    @Transactional
    @Override
    public Review create(Review obj) {
        Long id = insert(CREATE_REVIEW, obj.getContent(), obj.getIsPositive(), obj.getUserId(), obj.getFilmId());
        return findById(id).orElseThrow(() -> new InternalServerException("Failed to create review."));
    }

    @Transactional
    @Override
    public Review update(Review obj) {
        update(UPDATE_REVIEW, obj.getContent(), obj.getIsPositive(), obj.getReviewId());
        return findById(obj.getReviewId()).orElseThrow(() -> new InternalServerException("Review not found."));
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<Review> findByFilmId(Long filmId, int count) {

        if (filmId == null) {
            return findMany(TAKE_REVIEWS_LIST, count);
        } else {
            return findMany(TAKE_REVIEW_BY_FILM_ID, filmId, count);
        }
    }

    @Transactional
    @Override
    public void putLikeOrDislike(Long reviewId, Long userId, boolean isLike) {
        int reaction = 1;

        if (!isLike) {
            reaction = -1;
        }

        jdbc.update(UPDATE_REACTION, reaction, reviewId);
    }
}
