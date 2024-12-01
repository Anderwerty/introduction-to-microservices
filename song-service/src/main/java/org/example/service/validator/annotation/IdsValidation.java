package org.example.service.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.service.validator.IdsConstraintValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IdsConstraintValidator.class})
public @interface IdsValidation {
    String message() default "Invalid ids value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
