package com.epam.esm.validator;

import com.epam.esm.entity.Tag;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.regex.Pattern;

import static com.epam.esm.validator.ValidationError.NAME;

@Component
public class TagValidator {
    private static final String NAME_REGEX = "^\\p{Alpha}{3,50}$";

    public Pair<Boolean, EnumSet<ValidationError>> validate(Tag tag) {
        EnumSet<ValidationError> validationErrors = EnumSet.noneOf(ValidationError.class);
        String name = tag.getName();
        boolean validationResult = name != null && Pattern.matches(NAME_REGEX, name);

        if (!validationResult) {
            validationErrors.add(NAME);
        }

        return Pair.of(validationResult, validationErrors);
    }
}
