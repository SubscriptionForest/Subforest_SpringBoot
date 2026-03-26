package com.subforest.validation;

import com.subforest.service.ValidCycleDays;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class CycleDaysValidator implements ConstraintValidator<ValidCycleDays, Integer> {
    private static final Set<Integer> ALLOWED = Set.of(30, 90, 180, 365);
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext ctx) {
        return value != null && ALLOWED.contains(value);
    }
}
