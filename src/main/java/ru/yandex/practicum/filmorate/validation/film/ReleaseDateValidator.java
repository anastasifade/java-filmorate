package ru.yandex.practicum.filmorate.validation.film;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.exceptions.MalformedDataException;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ReleaseDateConstraint releaseDate) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        if (releaseDate == null) {
            return true;
        }

        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new MalformedDataException(String.format("Release date cannot be before %s.",
                    MIN_RELEASE_DATE));
        }

        return true;
    }
}
