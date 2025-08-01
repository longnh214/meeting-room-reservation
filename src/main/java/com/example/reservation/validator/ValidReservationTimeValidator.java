package com.example.reservation.validator;

import com.example.reservation.annotation.ValidReservationTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class ValidReservationTimeValidator
        implements ConstraintValidator<ValidReservationTime, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            LocalDateTime start = getTime(value, "getStartTime", "getNewStartTime");
            LocalDateTime end = getTime(value, "getEndTime", "getNewEndTime");

            if (start == null || end == null) return true;

            return end.isAfter(start);
        } catch (Exception e) {
            return true;
        }
    }

    private LocalDateTime getTime(Object value, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = value.getClass().getMethod(name);
                return (LocalDateTime) m.invoke(value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return null;
    }
}