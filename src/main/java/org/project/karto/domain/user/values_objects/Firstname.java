package org.project.karto.domain.user.values_objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Firstname(String firstname) {

    public static final int MIN_SIZE = 2;

    public static final int MAX_SIZE = 48;

    private static final String FIRSTNAME_REGEX = "^[A-Za-z]+$";

    private static final Pattern FIRST_NAME_PATTERN = Pattern.compile(FIRSTNAME_REGEX);

    public Firstname {
        validate(firstname);
    }

    public static void validate(String firstname) {
        if (firstname == null) throw new IllegalArgumentException("Firstname must not be null.");
        if (firstname.isBlank()) throw new IllegalArgumentException("First Name should`t be blank.");
        if (firstname.length() < MIN_SIZE || firstname.length() > MAX_SIZE)
            throw new IllegalArgumentException("Fist Name should`t be smaller than 3 characters and greater than 25.");
        Matcher matcher = FIRST_NAME_PATTERN.matcher(firstname);
        if (!matcher.matches()) throw new IllegalArgumentException("First Name should match regex.");
    }

    @Override
    public String toString() {
        return firstname;
    }
}
