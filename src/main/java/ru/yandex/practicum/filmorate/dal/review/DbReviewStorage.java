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
    private static final String UPDATE_REVIEW = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String TAKE_REVIEWS_LIST = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String TAKE_REVIEW_BY_FILM_ID = "SELECT * FROM reviews WHERE film_id = ? " +
            "ORDER BY useful DESC LIMIT ?";
    private static final String UPDATE_REACTION =  "MERGE INTO review_likes (review_id, user_id, is_like) " +
            "KEY (review_id, user_id) VALUES (?, ?, ?)";
    private static final String UPDATE_USEFUL = "UPDATE reviews SET useful = (" +
            "  SELECT COALESCE(SUM(CASE WHEN is_like = TRUE THEN 1 ELSE -1 END), 0) " +
            "  FROM review_likes WHERE review_id = ?" +
            ") WHERE review_id = ?";
    private static final String DELETE_REACTION =
            "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = ?";

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
        jdbc.update(UPDATE_REACTION, reviewId, userId, isLike);
        jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
    }

    @Override
    protected String getDeleteQuery() {
        return String.format("DELETE FROM %s WHERE review_id = ?;", table);
    }

    @Override
    protected String getFindByIdQuery() {
        return String.format("SELECT * FROM %s WHERE review_id = ?;", table);
    }

    @Override
    protected String getFindAllQuery() {
        return String.format("SELECT * FROM %s ORDER BY review_id;", table);
    }

    @Transactional
    public boolean deleteLike(Long reviewId, Long userId) {
        int rowsDelete = jdbc.update(DELETE_REACTION, reviewId, userId, true);

        if (rowsDelete > 0) {
            jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
            return true;
        }

        return false;
    }

    @Transactional
    public boolean deleteDislike(Long reviewId, Long userId) {
        int rowsDelete = jdbc.update(DELETE_REACTION, reviewId, userId, false);

        if (rowsDelete > 0) {
            jdbc.update(UPDATE_USEFUL, reviewId, reviewId);
            return true;
        }

        return false;
    }
}
