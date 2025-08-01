package org.project.karto.domain.common.value_objects;

import org.project.karto.domain.common.exceptions.IllegalDomainArgumentException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Email(String email) {

    private static final String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private static final Pattern pattern = Pattern.compile(emailRegex);

    public static final int MAX_SIZE = 256;

    public Email {
        validate(email);
    }

    public static void validate(String email) {
        if (Objects.isNull(email)) throw new IllegalDomainArgumentException("Email can`t be null");
        if (email.isBlank()) throw new IllegalDomainArgumentException("Email can`t be blank");
        if (email.length() > MAX_SIZE) throw new IllegalDomainArgumentException("Email is too long");

        String[] splitEmail = email.split("@");

        if (splitEmail.length != 2) throw new IllegalDomainArgumentException("Invalid email format.");
        if (splitEmail[0].isEmpty() || splitEmail[0].length() > 64)
            throw new IllegalDomainArgumentException("Invalid email format.");

        if (splitEmail[1].isEmpty() || (splitEmail[1].length() < 3 || splitEmail[1].length() > 252))
            throw new IllegalDomainArgumentException("Invalid email format.");

        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches())
            throw new IllegalDomainArgumentException("Email format error");
    }

    @Override
    public String toString() {
        return email;
    }
}
