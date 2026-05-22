package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmResultSetMapper implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("id");
            if (!filmMap.containsKey(id)) {
                Film film = Film.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .description(rs.getString("description"))
                        .duration(rs.getInt("duration"))
                        .mpa(new MPA(rs.getLong("mpa_id"), rs.getString("mpa_name")))
                        .genres(new LinkedHashSet<>())
                        .directors(new LinkedHashSet<>())
                        .build();
                filmMap.put(id, film);
            }

            if (rs.getString("genre_name") != null) {
                filmMap.get(id).getGenres()
                        .add(new Genre(rs.getLong("genre_id"), rs.getString("genre_name")));
            }

            if (rs.getString("director_name") != null) {
                filmMap.get(id).getDirectors()
                        .add(new Director(rs.getLong("director_id"),
                                rs.getString("director_name")));
            }
        }

        return new ArrayList<>(filmMap.values());
    }
}
