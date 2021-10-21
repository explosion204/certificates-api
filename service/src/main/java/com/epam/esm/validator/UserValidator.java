package com.epam.esm.validator;

import com.epam.esm.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.epam.esm.validator.ValidationError.INVALID_PASSWORD;
import static com.epam.esm.validator.ValidationError.INVALID_USERNAME;

@Component
public class UserValidator {
    private static final String USERNAME_REGEX = "^[\\p{LD}_]{8,32}$";
    private static final String PASSWORD_REGEX = "^(?=.*\\p{Alpha})(?=.*\\d)[\\p{Alnum}]{8,32}$";

    public List<ValidationError> validate(User user) {
        List<ValidationError> validationErrors = new ArrayList<>();
        String username = user.getUsername();
        String password = user.getPassword();

        boolean usernameIsValid = validateUsername(username);
        if (!usernameIsValid) {
            validationErrors.add(INVALID_USERNAME);
        }

        boolean passwordIsValid = validatePassword(password);
        if (!passwordIsValid) {
            validationErrors.add(INVALID_PASSWORD);
        }

        return validationErrors;
    }

    private boolean validateUsername(String username) {
        return Pattern.matches(USERNAME_REGEX, username);
    }

    private boolean validatePassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }
}
