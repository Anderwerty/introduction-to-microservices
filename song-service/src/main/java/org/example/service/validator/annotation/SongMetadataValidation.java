package org.example.service.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.service.validator.SongMetadataValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SongMetadataValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SongMetadataValidation {
    String message() default "Invalid field combination";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
