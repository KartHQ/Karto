package org.project.karto.domain.user.entities;

import org.project.karto.domain.common.enumerations.Role;
import org.project.karto.domain.common.value_objects.KeyAndCounter;
import org.project.karto.domain.user.values_objects.PersonalData;
import org.project.karto.domain.common.value_objects.Phone;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private PersonalData personalData;
    private final Role role;
    private boolean isVerified;
    private boolean is2FAEnabled;
    private KeyAndCounter keyAndCounter;
    private final LocalDateTime creationDate;
    private final LocalDateTime lastUpdated;

    private User(
            UUID id,
            PersonalData personalData,
            boolean isVerified,
            boolean is2FAEnabled,
            KeyAndCounter keyAndCounter,
            LocalDateTime creationDate, LocalDateTime lastUpdated) {

        if (id == null || personalData == null ||
                creationDate == null || keyAndCounter == null || lastUpdated == null) {
            throw new IllegalArgumentException("User data is null");
        }

        this.id = id;
        this.personalData = personalData;
        this.role = Role.CUSTOMER;
        this.isVerified = isVerified;
        this.is2FAEnabled = is2FAEnabled;
        this.keyAndCounter = keyAndCounter;
        this.creationDate = creationDate;
        this.lastUpdated = lastUpdated;
    }

    public static User of(PersonalData personalData, String key) {
        return new User(UUID.randomUUID(),
                personalData,
                false,
                false,
                new KeyAndCounter(key, 0),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    public static User fromRepository(
            UUID id,
            PersonalData personalData,
            boolean isEnabled,
            boolean is2FAVerified,
            KeyAndCounter keyAndCounter,
            LocalDateTime creationDate,
            LocalDateTime lastUpdated) {

        return new User(id,
                personalData,
                isEnabled,
                is2FAVerified,
                keyAndCounter,
                creationDate,
                lastUpdated);
    }

    public UUID id() {
        return id;
    }

    public PersonalData personalData() {
        return personalData;
    }

    public Role role() {
        return role;
    }

    public void registerPhoneForVerification(Phone phone) {
        if (phone == null)
            throw new IllegalArgumentException("Phone is null");

        if (this.personalData.phone().isPresent())
            throw new IllegalArgumentException("Phone number already registered.");

        this.personalData = new PersonalData(
                personalData().firstname(),
                personalData().surname(),
                phone.phoneNumber(),
                personalData().password().orElse(null),
                personalData().email(),
                personalData().birthDate()
        );
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void enable() {
        if (isVerified)
            throw new IllegalStateException("You can`t active already verified user.");
        if (keyAndCounter.counter() == 0)
            throw new IllegalStateException("It is prohibited to activate an account that has not been verified.");
        this.isVerified = true;
    }

    public void enable2FA() {
        if (!isVerified)
            throw new IllegalStateException("You can`t enable 2FA on not verified account");
        if (keyAndCounter.counter() == 0 || keyAndCounter.counter() == 1)
            throw new IllegalStateException("Counter need to be incremented");
        if (is2FAEnabled)
            throw new IllegalStateException("You can`t activate 2FA twice");

        this.is2FAEnabled = true;
    }

    public boolean is2FAEnabled() {
        return is2FAEnabled;
    }

    public void disable() {
        if (!isVerified)
            throw new IllegalStateException("You can't deactivate a user who is already deactivated.");
        this.isVerified = false;
    }

    public KeyAndCounter keyAndCounter() {
        return keyAndCounter;
    }

    public void incrementCounter() {
        this.keyAndCounter = new KeyAndCounter(keyAndCounter.key(), keyAndCounter.counter() + 1);
    }

    public LocalDateTime creationDate() {
        return creationDate;
    }

    public LocalDateTime lastUpdated() {
        return lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isVerified == user.isVerified &&
                Objects.equals(id, user.id) &&
                Objects.equals(personalData, user.personalData) &&
                Objects.equals(creationDate, user.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, personalData, isVerified, creationDate);
    }

    @Override
    public String toString() {
        return """
                User: {
                    ID: %s,
                    Firstname: %s,
                    Lastname: %s,
                    Phone: %s,
                    Email: %s,
                    Birth date: %s
                }
                """.formatted(id.toString(),
                personalData.firstname(),
                personalData.surname(),
                personalData.phone(),
                personalData.email(),
                personalData.birthDate());
    }
}
