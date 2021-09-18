package com.epam.esm.validator;

import com.epam.esm.entity.GiftCertificate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EnumSet;
import java.util.regex.Pattern;

import static com.epam.esm.validator.ValidationError.*;

@Component
public class GiftCertificateValidator {
    private static final String NAME_REGEX = "^[\\w\\s,.]{3,50}$";
    private static final String DESCRIPTION_REGEX = "^[\\w\\s,.]{10,100}$";

    public Pair<Boolean, EnumSet<ValidationError>> validate(GiftCertificate certificate, boolean nullValid) {
        EnumSet<ValidationError> validationErrors = EnumSet.noneOf(ValidationError.class);
        String name = certificate.getName();
        String description = certificate.getDescription();
        BigDecimal price = certificate.getPrice();
        Duration duration = certificate.getDuration();

        boolean validationResult = nullValid && name == null || validateName(name);
        if (!validationResult) {
            validationErrors.add(NAME);
        }

        validationResult &= nullValid && description == null || validateDescription(description);
        if (!validationResult) {
            validationErrors.add(DESCRIPTION);
        }

        validationResult &= nullValid && price == null || validatePrice(price);
        if (!validationResult) {
            validationErrors.add(PRICE);
        }

        validationResult &= nullValid && duration == null || validateDuration(duration);
        if (!validationResult) {
            validationErrors.add(DURATION);
        }

        return Pair.of(validationResult, validationErrors);
    }

    private boolean validateName(String name) {
        return Pattern.matches(NAME_REGEX, name);
    }

    private boolean validateDescription(String description) {
        return Pattern.matches(DESCRIPTION_REGEX, description);
    }

    private boolean validatePrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean validateDuration(Duration duration) {
        return duration.toDays() > 0;
    }
}
