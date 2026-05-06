package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbTests {

    private final MpaDbStorage mpaStorage;

    @Test
    public void findMpaByIdTest() {
        Optional<MPA> mpaOptional = mpaStorage.findById(1);
        assertThat(mpaOptional)
                .isPresent()
                .satisfies(mpa ->
                        assertThat(mpa.get()).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "G"));
    }

    @Test
    public void findAllMpaTest() {
        List<MPA> mpas = mpaStorage.findAll().stream().toList();
        Assertions.assertFalse(mpas.isEmpty());
        Assertions.assertTrue(mpas.size() == 5);

        mpas.stream()
                .peek(mpa -> assertThat(mpa).hasFieldOrProperty("id")
                        .hasFieldOrProperty("name")
                        .satisfies(m -> assertThat(m.getId()).isNotNull())
                        .satisfies(m -> assertThat(m.getName()).isNotBlank()));
    }
}
