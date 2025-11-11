package org.example.service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.service.validator.annotation.PositiveNumberString;


public class PositiveNumberStringValidator implements ConstraintValidator<PositiveNumberString, String> {

    private String message;

    @Override
    public void initialize(PositiveNumberString constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (value == null || value.isBlank()) {
            valid = false;
        } else {
            try {
                int number = Integer.parseInt(value);
                valid = number > 0;
            } catch (NumberFormatException e) {
                valid = false;
            }
        }
        if(!valid){
            buildViolationMessage(context, value);
        }
        return valid;

    }

    private void buildViolationMessage(ConstraintValidatorContext context, String value) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.format(message, value))
                .addConstraintViolation();
    }
}
