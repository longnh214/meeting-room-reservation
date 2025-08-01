package com.example.reservation.annotation;

import com.example.reservation.validator.HalfHourValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HalfHourValidator.class)
@Documented
public @interface HalfHour {
    String message() default "예약 시간은 00분 또는 30분 단위만 가능합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}