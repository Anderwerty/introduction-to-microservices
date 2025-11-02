package org.example.service.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.service.validator.PositiveNumberStringValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveNumberStringValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveNumberString {
    String message() default "Invalid value '%s' for ID. Must be a positive integer";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
