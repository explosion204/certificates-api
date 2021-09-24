package com.epam.esm.validator;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.regex.Pattern;

import static com.epam.esm.validator.ValidationError.NAME;

@Component
public class TagValidator {
    private static final String NAME_REGEX = "^\\p{Alnum}{3,50}$";

    public Pair<Boolean, EnumSet<ValidationError>> validate(String tagName) {
        EnumSet<ValidationError> validationErrors = EnumSet.noneOf(ValidationError.class);
        boolean nameIsValid = tagName != null && Pattern.matches(NAME_REGEX, tagName);

        if (!nameIsValid) {
            validationErrors.add(NAME);
        }

        return Pair.of(nameIsValid, validationErrors);
    }


}
