package org.example.service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.service.validator.annotation.IdValidation;
import org.springframework.stereotype.Component;

@Component
public class IdConstraintValidator implements ConstraintValidator<IdValidation, String> {
    @Override
    public boolean isValid(String id, ConstraintValidatorContext validatorContext) {
        if (id == null || id.isBlank()) {
            validatorContext.disableDefaultConstraintViolation();
            validatorContext
                    .buildConstraintViolationWithTemplate("Id is null or blank")
                    .addConstraintViolation();
            return false;
        }
        try {
            int identifier = Integer.parseInt(id);
            if (identifier <= 0) {
                validatorContext.disableDefaultConstraintViolation();
                validatorContext
                        .buildConstraintViolationWithTemplate(String.format("Id [%s] is not positive", id))
                        .addConstraintViolation();
                return false;
            }

        } catch (Exception e) {
            validatorContext.disableDefaultConstraintViolation();
            validatorContext
                    .buildConstraintViolationWithTemplate(String.format("Id [%s] is not int type", id))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
