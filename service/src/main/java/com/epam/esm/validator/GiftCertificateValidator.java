package com.epam.esm.validator;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.epam.esm.validator.ValidationError.*;

@Component
public class GiftCertificateValidator {
    private static final String NAME_REGEX = "^[\\p{LD}\\s,.]{3,50}$";
    private static final String DESCRIPTION_REGEX = "^[\\p{LD}\\s,.]{10,100}$";
    private static final int PRICE_MIN_VALUE = 0;
    private static final int DURATION_MIN_VALUE = 0;

    public List<ValidationError> validate(GiftCertificate certificate, boolean nullValid) {
        List<ValidationError> validationErrors = new ArrayList<>();
        String name = certificate.getName();
        String description = certificate.getDescription();
        BigDecimal price = certificate.getPrice();
        Duration duration = certificate.getDuration();

        boolean nameIsValid = (nullValid && name == null) || (name != null && validateName(name));
        if (!nameIsValid) {
            validationErrors.add(INVALID_NAME);
        }

        boolean descriptionIsValid = (nullValid && description == null) || (description != null
                && validateDescription(description));
        if (!descriptionIsValid) {
            validationErrors.add(INVALID_DESCRIPTION);
        }

        boolean priceIsValid = (nullValid && price == null) || (price != null && validatePrice(price));
        if (!priceIsValid) {
            validationErrors.add(INVALID_PRICE);
        }

        boolean durationIsValid = (nullValid && duration == null) || (duration != null && validateDuration(duration));
        if (!durationIsValid) {
            validationErrors.add(INVALID_DURATION);
        }

        return validationErrors;
    }

    private boolean validateName(String name) {
        return Pattern.matches(NAME_REGEX, name);
    }

    private boolean validateDescription(String description) {
        return Pattern.matches(DESCRIPTION_REGEX, description);
    }

    private boolean validatePrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) > PRICE_MIN_VALUE;
    }

    private boolean validateDuration(Duration duration) {
        return duration.toDays() > DURATION_MIN_VALUE;
    }
}
