package com.epam.esm.validator;

import com.epam.esm.entity.Tag;
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
        boolean validationResult = tagName != null && Pattern.matches(NAME_REGEX, tagName);

        if (!validationResult) {
            validationErrors.add(NAME);
        }

        return Pair.of(validationResult, validationErrors);
    }


}
