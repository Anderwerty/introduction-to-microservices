package org.example.service.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.service.validator.IdConstraintValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IdConstraintValidator.class})
public @interface IdValidation {
    String message() default "Invalid id value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
