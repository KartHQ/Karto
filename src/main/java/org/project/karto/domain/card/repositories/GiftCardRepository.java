package org.project.karto.domain.card.repositories;

import java.util.List;
import java.util.UUID;

import org.project.karto.domain.card.entities.GiftCard;
import org.project.karto.domain.card.value_objects.BuyerID;
import org.project.karto.domain.card.value_objects.CardID;
import org.project.karto.domain.card.value_objects.OwnerID;
import org.project.karto.domain.card.value_objects.StoreID;
import org.project.karto.domain.card.value_objects.UserActivitySnapshot;
import org.project.karto.domain.common.containers.Result;


public interface GiftCardRepository {

    Result<Integer, Throwable> save(GiftCard giftCard);

    Result<Integer, Throwable> update(GiftCard giftCard);

    Result<GiftCard, Throwable> findBy(CardID cardID);

    Result<List<GiftCard>, Throwable> findBy(BuyerID buyerID);

    Result<List<GiftCard>, Throwable> findBy(OwnerID ownerID);

    Result<List<GiftCard>, Throwable> findBy(StoreID storeID);

    Result<UserActivitySnapshot, Throwable> findBy(UUID userID);
}
