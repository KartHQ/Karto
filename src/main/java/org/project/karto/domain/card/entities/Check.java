package org.project.karto.domain.card.entities;

import org.project.karto.domain.card.enumerations.PaymentType;
import org.project.karto.domain.card.value_objects.*;
import org.project.karto.domain.common.annotations.Nullable;
import org.project.karto.domain.common.exceptions.IllegalDomainArgumentException;
import org.project.karto.domain.common.value_objects.Amount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Check {
    private final UUID id;
    private final long orderID;
    private final BuyerID buyerID;
    private final @Nullable StoreID storeID;
    private final @Nullable CardID cardID;
    private final Amount totalAmount;
    private final Currency currency;
    private final PaymentType paymentType;
    private final InternalFeeAmount internalFee;
    private final ExternalFeeAmount externalFee;
    private final PaymentSystem paymentSystem;
    private final ExternalPayeeDescription description;
    private final LocalDateTime creationDate;

    private Check(
            UUID id,
            long orderID,
            BuyerID buyerID,
            StoreID storeID,
            CardID cardID,
            Amount totalAmount,
            Currency currency,
            PaymentType paymentType,
            InternalFeeAmount internalFee,
            ExternalFeeAmount externalFee,
            PaymentSystem paymentSystem,
            ExternalPayeeDescription description,
            LocalDateTime creationDate) {

        this.id = id;
        this.orderID = orderID;
        this.buyerID = buyerID;
        this.storeID = storeID;
        this.cardID = cardID;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.paymentType = paymentType;
        this.internalFee = internalFee;
        this.externalFee = externalFee;
        this.paymentSystem = paymentSystem;
        this.description = description;
        this.creationDate = creationDate;
    }

    static Check cardPurchaseCheck(
            long orderID,
            BuyerID buyerID,
            StoreID storeID,
            Amount spentAmount,
            Currency currency,
            PaymentType paymentType,
            InternalFeeAmount internalFee,
            ExternalFeeAmount externalFee,
            PaymentSystem paymentSystem,
            ExternalPayeeDescription description) {

        validateInputs(orderID, buyerID, spentAmount, currency, paymentType, internalFee, externalFee, paymentSystem, description);
        return new Check(UUID.randomUUID(), orderID, buyerID, storeID, null, spentAmount,
                currency, paymentType, internalFee, externalFee, paymentSystem, description, LocalDateTime.now());
    }

    static Check paymentCheck(
            long orderID,
            BuyerID buyerID,
            StoreID storeID,
            CardID cardID,
            Amount spentAmount,
            Currency currency,
            PaymentType paymentType,
            InternalFeeAmount internalFee,
            PaymentSystem paymentSystem,
            ExternalPayeeDescription description) {

        ExternalFeeAmount zeroedFee = new ExternalFeeAmount(BigDecimal.ZERO);
        if (cardID == null) throw new IllegalDomainArgumentException("CardID can`t be null");

        validateInputs(orderID, buyerID, spentAmount, currency, paymentType, internalFee, zeroedFee, paymentSystem, description);
        return new Check(UUID.randomUUID(), orderID, buyerID, storeID, cardID, spentAmount, currency, paymentType,
                internalFee, zeroedFee, paymentSystem, description, LocalDateTime.now());
    }

    public static Check fromRepository(
            UUID checkID,
            long orderID,
            BuyerID buyerID,
            StoreID storeID,
            CardID cardID,
            Amount spentAmount,
            Currency currency,
            PaymentType paymentType,
            InternalFeeAmount internalFee,
            ExternalFeeAmount externalFee,
            PaymentSystem paymentSystem,
            ExternalPayeeDescription description,
            LocalDateTime creationDate) {

        return new Check(checkID, orderID, buyerID, storeID, cardID, spentAmount, currency, paymentType, internalFee,
                externalFee, paymentSystem, description, creationDate);
    }

    private static void validateInputs(
            long orderID,
            BuyerID buyerID,
            Amount spentAmount,
            Currency currency,
            PaymentType paymentType,
            InternalFeeAmount internalFee,
            ExternalFeeAmount externalFee,
            PaymentSystem paymentSystem,
            ExternalPayeeDescription description) {

        if (orderID <= 0) throw new IllegalDomainArgumentException("orderID must be positive");
        if (buyerID == null) throw new IllegalDomainArgumentException("buyerID must not be null");
        if (spentAmount == null) throw new IllegalDomainArgumentException("spentAmount must not be null");
        if (currency == null) throw new IllegalDomainArgumentException("currency must not be null");
        if (paymentType == null) throw new IllegalDomainArgumentException("paymentType must not be null");
        if (internalFee == null) throw new IllegalDomainArgumentException("internalFee must not be null");
        if (externalFee == null) throw new IllegalDomainArgumentException("externalFee must not be null");
        if (paymentSystem == null) throw new IllegalDomainArgumentException("paymentSystem must not be null");
        if (description == null) throw new IllegalDomainArgumentException("description must not be null");
    }

    public UUID id() {
        return id;
    }

    public long orderID() {
        return orderID;
    }

    public BuyerID buyerID() {
        return buyerID;
    }

    public Optional<CardID> cardID() {
        return Optional.ofNullable(cardID);
    }

    public Optional<StoreID> storeID() {
        return Optional.ofNullable(storeID);
    }

    public Amount totalAmount() {
        return totalAmount;
    }

    public Currency currency() {
        return currency;
    }

    public PaymentType paymentType() {
        return paymentType;
    }

    public InternalFeeAmount internalFee() {
        return internalFee;
    }

    public ExternalFeeAmount externalFee() {
        return externalFee;
    }

    public PaymentSystem paymentSystem() {
        return paymentSystem;
    }

    public LocalDateTime creationDate() {
        return creationDate;
    }

    public ExternalPayeeDescription description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Check other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}