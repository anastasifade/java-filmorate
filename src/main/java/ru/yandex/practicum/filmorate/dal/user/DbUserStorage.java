package ru.yandex.practicum.filmorate.dal.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.DbStorage;
import ru.yandex.practicum.filmorate.exceptions.FailedToDeleteException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.Collection;

@Slf4j
@Repository
@Primary
@Transactional
public class DbUserStorage extends DbStorage<User> implements UserStorage {
    private static final String INSERT_USER = """
            INSERT INTO users (login, email, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_USER = """
            UPDATE users
            SET login = ?, email = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    private static final String FIND_LOGIN = """
            SELECT *
            FROM users
            WHERE login = ?
            """;

    private static final String FIND_EMAIL = """
            SELECT *
            FROM users
            WHERE email = ?
            """;

    private static final String FIND_FRIENDS = """
            SELECT u.*
            FROM   users u
            JOIN   friends f ON u.id = f.friend_id
            WHERE  f.user_id = ?
            """;

    private static final String FIND_COMMON_FRIENDS = """
            SELECT u.*
            FROM   users u
            JOIN   friends f1 ON u.id = f1.friend_id
            JOIN   friends f2 ON u.id = f2.friend_id
            WHERE  f1.user_id = ? AND f2.user_id = ?
            """;

    private static final String ADD_FRIEND = """
            MERGE INTO friends (user_id, friend_id) KEY (user_id, friend_id)
            VALUES (?, ?)
            """;

    private static final String DELETE_FRIEND = """
            DELETE FROM friends
            WHERE user_id = ? AND friend_id = ?
            """;

    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super("users", jdbc, mapper);
    }

    @Override
    public User create(User obj) {
        log.trace("DbUserStorage received INSERT request for user: {}.", obj);
        Long id = insert(INSERT_USER,
                obj.getLogin(),
                obj.getEmail(),
                obj.getName(),
                Date.valueOf(obj.getBirthday()));

        log.trace("User created, id: {}", id);
        return findById(id).orElseThrow(() -> new InternalServerException("Failed to create user."));
    }

    @Override
    public User update(User obj) {
        update(UPDATE_USER,
                obj.getLogin(),
                obj.getEmail(),
                obj.getName(),
                Date.valueOf(obj.getBirthday()),
                obj.getId());

        return findById(obj.getId()).orElseThrow(() ->
                new InternalServerException("Failed to update user information."));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isLoginOccupied(String login) {
        return findOne(FIND_LOGIN, login).isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isEmailOccupied(String email) {
        return findOne(FIND_EMAIL, email).isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<User> getFriends(Long userId) {
        return findMany(FIND_FRIENDS, userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<User> getCommonFriends(Long user1, Long user2) {
        return findMany(FIND_COMMON_FRIENDS, user1, user2);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        update(ADD_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        try {
            delete(DELETE_FRIEND, userId, friendId);
        } catch (FailedToDeleteException e) {
            log.warn("Failed to delete user [id={}] from friend list of user [id={}].", friendId, userId);
        }
    }
}
