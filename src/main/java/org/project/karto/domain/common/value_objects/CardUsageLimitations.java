package org.project.karto.domain.common.value_objects;

import org.project.karto.domain.common.exceptions.IllegalDomainArgumentException;

import java.time.Period;
import java.util.Objects;

public class CardUsageLimitations {
    private final Period expirationPeriod;
    private final int maxUsageCount;

    public static final int MIN_DAYS = 30;
    public static final int MAX_DAYS = 92;

    private CardUsageLimitations(Period expirationPeriod, int maxUsageCount) {
        if (expirationPeriod == null || expirationPeriod.isZero() || expirationPeriod.isNegative())
            throw new IllegalDomainArgumentException("Expiration period must be a positive non-zero value.");

        if (expirationPeriod.getYears() != 0 || expirationPeriod.getMonths() != 0)
            throw new IllegalDomainArgumentException("Expiration period must be specified in days only.");

        int days = expirationPeriod.getDays();
        if (days < MIN_DAYS || days > MAX_DAYS)
            throw new IllegalDomainArgumentException("Expiration period must be between 30 and 92 days.");

        if (maxUsageCount < 1 || maxUsageCount > 10) throw new IllegalDomainArgumentException("Usage count must be between 1 and 10.");

        this.expirationPeriod = expirationPeriod;
        this.maxUsageCount = maxUsageCount;
    }

    public static CardUsageLimitations of(int days, int maxUsageCount) {
        if (days < MIN_DAYS || days > MAX_DAYS)
            throw new IllegalDomainArgumentException("Expiration period must be between 30 and 92 days.");

        return new CardUsageLimitations(Period.ofDays(days), maxUsageCount);
    }

    public int expirationDays() {
        return expirationPeriod.getDays();
    }

    public Period expirationPeriod() {
        return expirationPeriod;
    }

    public int maxUsageCount() {
        return maxUsageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CardUsageLimitations that = (CardUsageLimitations) o;
        return maxUsageCount == that.maxUsageCount && Objects.equals(expirationPeriod, that.expirationPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expirationPeriod, maxUsageCount);
    }
}
