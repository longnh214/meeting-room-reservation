package com.example.reservation.validator;

import com.example.reservation.annotation.HalfHour;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class HalfHourValidator implements ConstraintValidator<HalfHour, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int minute = value.getMinute();
        return (minute == 0 || minute == 30);
    }
}