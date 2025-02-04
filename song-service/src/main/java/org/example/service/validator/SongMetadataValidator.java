package org.example.service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.service.rest.dto.SongMetaDataDto;
import org.example.service.validator.annotation.SongMetadataValidation;
import org.springframework.stereotype.Component;

@Component
public class SongMetadataValidator implements ConstraintValidator<SongMetadataValidation, SongMetaDataDto> {
    @Override
    public boolean isValid(SongMetaDataDto dto, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (dto.getId() == null) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Numeric, must match an existing Resource ID.")
                    .addPropertyNode("id")
                    .addConstraintViolation();
        }
        if (dto.getName() == null || dto.getName().isEmpty() || dto.getName().length() > 100) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1-100 characters text")
                    .addPropertyNode("name")
                    .addConstraintViolation();
        }
        if (dto.getArtist() == null || dto.getArtist().isEmpty() || dto.getArtist().length() > 100) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1-100 characters text")
                    .addPropertyNode("artist")
                    .addConstraintViolation();
        }
        if (dto.getAlbum() == null || dto.getAlbum().isEmpty() || dto.getAlbum().length() > 100) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("1-100 characters text")
                    .addPropertyNode("album")
                    .addConstraintViolation();
        }
        if (dto.getDuration() == null || !dto.getDuration().matches("^(0[0-9]|[1-5][0-9]):[0-5][0-9]$")) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Format mm:ss, with leading zeros.")
                    .addPropertyNode("duration")
                    .addConstraintViolation();
        }
        if (dto.getYear() == null || !dto.getYear().matches("^(19[0-9]{2}|20[0-9]{2})$")) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("YYYY format between 1900-2099.")
                    .addPropertyNode("year")
                    .addConstraintViolation();
        }
        return isValid;
    }

}
