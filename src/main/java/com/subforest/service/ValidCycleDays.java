package com.subforest.service;

import com.subforest.validation.CycleDaysValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CycleDaysValidator.class)
public @interface ValidCycleDays {
    String message() default "반복 주기는 30/90/180/365 중 하나여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
