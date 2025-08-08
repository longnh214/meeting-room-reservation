package com.example.reservation.validator;

import com.example.reservation.annotation.AfterCurrentTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class AfterCurrentTimeValidator
        implements ConstraintValidator<AfterCurrentTime, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = getTime(value, "getStartTime", "getNewStartTime");

            return start.isAfter(now);
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
