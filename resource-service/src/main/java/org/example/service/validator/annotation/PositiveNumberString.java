package org.example.service.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.service.validator.PositiveNumberStringValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveNumberStringValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveNumberString {
    String message() default "ID must be a positive integer number in string format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
