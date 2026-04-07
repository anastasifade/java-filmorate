package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public void initialize(NullOrNotBlank constraint) {
    }

    @Override
    public boolean isValid(String field, ConstraintValidatorContext cxt) {
        return (field == null || !field.isBlank());
    }
}
