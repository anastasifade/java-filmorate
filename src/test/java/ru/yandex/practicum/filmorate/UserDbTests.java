package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.user.DbUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({DbUserStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTests {

    private final DbUserStorage userStorage;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testLoginUniqueness() {
        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .satisfies(userOpt ->
                        Assertions.assertTrue(userStorage.isLoginOccupied(userOpt.get().getLogin())));

        String unoccupiedLogin = "unoccupiedLogin";
        Assertions.assertFalse(userStorage.isLoginOccupied(unoccupiedLogin));
    }

    @Test
    public void testEmailUniqueness() {
        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .satisfies(userOpt ->
                        Assertions.assertTrue(userStorage.isEmailOccupied(userOpt.get().getEmail())));

        String unoccupiedEmail = "unoccupiedEmail@mail.org";
        Assertions.assertFalse(userStorage.isEmailOccupied(unoccupiedEmail));
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.findAll().stream().toList();
        Assertions.assertFalse(users.isEmpty());

        assertThat(users.getFirst()).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void createUserTest() {
        String login = "a";
        String email = "a@a.a";
        LocalDate birthday = LocalDate.of(1987, 1, 1);

        User newUser = User.builder()
                .name(login)
                .login(login)
                .email(email)
                .birthday(birthday)
                .build();

        newUser = userStorage.create(newUser);
        assertThat(newUser)
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("name", login)
                .hasFieldOrPropertyWithValue("login", login)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("birthday", birthday)
                .satisfies(user -> assertThat(user.getId()).isNotNull());
    }

    @Test
    public void updateUserTest() {
        Optional<User> userOptional = userStorage.findById(1);
        assertThat(userOptional).isPresent();

        User updateUser = userOptional.get();

        String newName = "newName";
        String newEmail = "new@email.address";
        String oldLogin = updateUser.getLogin();

        updateUser.setName(newName);
        updateUser.setEmail(newEmail);

        updateUser = userStorage.update(updateUser);
        assertThat(updateUser)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", newName)
                .hasFieldOrPropertyWithValue("login", oldLogin)
                .hasFieldOrPropertyWithValue("email", newEmail);
    }

    @Test
    public void addAndRemoveFriendTest() {
        Optional<User> firstUserOpt = userStorage.findById(1);
        assertThat(firstUserOpt).isPresent();

        User user = firstUserOpt.get();

        User friend = User.builder()
                .name("friend")
                .login("friendLogin")
                .email("friend@email.fr")
                .birthday(LocalDate.of(1997, 2, 2))
                .build();
        friend = userStorage.create(friend);

        userStorage.addFriend(user.getId(), friend.getId());

        List<User> userFriends = userStorage.getFriends(user.getId()).stream().toList();
        Assertions.assertTrue(userFriends.size() > 0);
        assertThat(userFriends.getFirst()).hasFieldOrPropertyWithValue("id", friend.getId());

        List<User> friendFriends = userStorage.getFriends(friend.getId()).stream().toList();
        Assertions.assertTrue(friendFriends.isEmpty());

        userStorage.deleteFriend(user.getId(), friend.getId());

        userFriends = userStorage.getFriends(user.getId()).stream().toList();
        Assertions.assertTrue(userFriends.isEmpty());

        friendFriends = userStorage.getFriends(friend.getId()).stream().toList();
        Assertions.assertTrue(friendFriends.isEmpty());
    }

    @Test
    public void getCommonFriendsTest() {
        Optional<User> firstUserOpt = userStorage.findById(1);
        assertThat(firstUserOpt).isPresent();

        User commonFriend = firstUserOpt.get();

        User firstUser = User.builder()
                .name("first_user")
                .login("first_user")
                .email("first_user@email.fr")
                .birthday(LocalDate.of(1997, 2, 2))
                .build();
        firstUser = userStorage.create(firstUser);

        User secondUser = User.builder()
                .name("second_user")
                .login("second_user")
                .email("second_user@email.fr")
                .birthday(LocalDate.of(1997, 2, 2))
                .build();
        secondUser = userStorage.create(secondUser);

        userStorage.addFriend(firstUser.getId(), commonFriend.getId());
        userStorage.addFriend(secondUser.getId(), commonFriend.getId());

        List<User> commonFriends = userStorage.getCommonFriends(firstUser.getId(), secondUser.getId())
                .stream()
                .toList();

        Assertions.assertTrue(commonFriends.size() > 0);
        assertThat(commonFriends.getFirst()).satisfies(friend ->
                assertThat(friend.getId()).isEqualTo(commonFriend.getId()));
    }


}
