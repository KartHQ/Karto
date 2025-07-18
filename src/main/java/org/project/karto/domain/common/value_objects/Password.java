package org.project.karto.domain.common.value_objects;

import org.project.karto.domain.common.exceptions.IllegalDomainArgumentException;

import java.util.Objects;

public record Password(String password) {
    public static final int MIN_SIZE = 8;
    public static final int MAX_SIZE = 64;
    public static final String INVALID_PASSWORD_SIZE = "Invalid password size: min - %s, max - %s";

    public static void validate(String password) {
        if (Objects.isNull(password)) throw new IllegalDomainArgumentException("Password is null");
        if (password.isBlank()) throw new IllegalDomainArgumentException("Password is blank");
        if (password.length() < MIN_SIZE)
            throw new IllegalDomainArgumentException(INVALID_PASSWORD_SIZE.formatted(MIN_SIZE, MAX_SIZE));
        if (password.length() > MAX_SIZE)
            throw new IllegalDomainArgumentException(INVALID_PASSWORD_SIZE.formatted(MIN_SIZE, MAX_SIZE));
    }
}