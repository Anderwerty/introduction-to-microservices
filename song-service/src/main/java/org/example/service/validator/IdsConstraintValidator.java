package org.example.service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.example.service.validator.annotation.IdsValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdsConstraintValidator implements ConstraintValidator<IdsValidation, String> {

    private final int idsParameterLengthLimit;

    public IdsConstraintValidator(@Value("${ids.parameter.length.limit}") int idsParameterLengthLimit) {
        this.idsParameterLengthLimit = idsParameterLengthLimit;
    }

    @Override
    public boolean isValid(String idsQueryParameter, ConstraintValidatorContext context) {
        if (idsQueryParameter == null || idsQueryParameter.isEmpty()) {
            return true;
        }

        int length = idsQueryParameter.length();
        if (length >= idsParameterLengthLimit) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("Too long ids parameter length [%d]", length))
                    .addConstraintViolation();
            return false;
        }

        String[] ids = idsQueryParameter.split(",");
        for (String id : ids) {
            if (!StringUtils.isNumeric(id)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(String.format("Id [%s] is not a number", id))
                        .addConstraintViolation();
                return false;
            }
            if (Integer.parseInt(id) <= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(String.format("Id [%s] is not a positive int", id))
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
