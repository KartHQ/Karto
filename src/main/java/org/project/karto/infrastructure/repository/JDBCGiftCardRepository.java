package org.project.karto.infrastructure.repository;

import org.project.karto.application.dto.gift_card.CardDTO;
import org.project.karto.domain.common.interfaces.Pageable;
import com.hadzhy.jetquerious.jdbc.JetQuerious;
import com.hadzhy.jetquerious.sql.QueryForge;
import jakarta.enterprise.context.ApplicationScoped;
import org.project.karto.domain.card.entities.GiftCard;
import org.project.karto.domain.card.enumerations.GiftCardStatus;
import org.project.karto.domain.card.repositories.GiftCardRepository;
import org.project.karto.domain.card.value_objects.*;
import org.project.karto.domain.common.containers.Result;
import org.project.karto.domain.common.value_objects.KeyAndCounter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static com.hadzhy.jetquerious.sql.QueryForge.insert;
import static com.hadzhy.jetquerious.sql.QueryForge.select;
import static org.project.karto.infrastructure.repository.JDBCCompanyRepository.mapTransactionResult;

@ApplicationScoped
public class JDBCGiftCardRepository implements GiftCardRepository {

    private final JetQuerious jet;

    static final String SAVE_GIFT_CARD = insert()
        .into("gift_card")
        .columns("id",
                "buyer_id",
                "owner_id",
                "store_id",
                "gift_card_status",
                "balance",
                "count_of_uses",
                "max_count_of_uses",
                "secret_key",
                "counter",
                "creation_date",
                "expiration_date",
                "last_usage",
                "version")
        .values()
        .build()
        .sql();

    static final String UPDATE_GIFT_CARD = QueryForge.update("gift_card")
        .set("""
                gift_card_status = ?,
                balance = ?,
                count_of_uses = ?,
                counter = ?,
                last_usage = ?,
                version = ?
                """)
        .where("id = ?")
        .and("version = ?")
        .build()
        .sql();

    static final String FIND_BY_CARD_ID = select()
        .all()
        .from("gift_card")
        .where("id = ?")
        .build()
        .sql();

    static final String FIND_BY_BUYER_ID = select()
        .all()
        .from("gift_card")
        .where("buyer_id = ?")
        .build()
        .sql();

    static final String FIND_BY_OWNER_ID = select()
        .all()
        .from("gift_card")
        .where("owner_id = ?")
        .build()
        .sql();

    static final String FIND_BY_STORE_ID = select()
        .all()
        .from("gift_card")
        .where("store_id = ?")
        .build()
        .sql();

    static final String FIND_ALL_AVAILABLE_CARDS = select()
        .column("c.id").as("id")
        .column("c.expiration_period_days").as("expiration_period_days")
        .column("c.max_usage_count").as("max_usage_count")
        .column("COUNT(gc.id) AS gift_card_count")
        .from("companies c")
        .leftJoin("gift_card gc", "gc.store_id = c.id")
        .groupBy("c.id, c.expiration_period_days, c.max_usage_count")
        .orderBy("gift_card_count DESC")
        .limitAndOffset()
        .sql();

    JDBCGiftCardRepository() {
        this.jet = JetQuerious.instance();
    }

    @Override
    public Result<Integer, Throwable> save(GiftCard giftCard) {
        return mapTransactionResult(jet.write(SAVE_GIFT_CARD,
                    giftCard.id(),
                    giftCard.buyerID(),
                    giftCard.ownerID().orElse(null),
                    giftCard.storeID().orElse(null),
                    giftCard.giftCardStatus(),
                    giftCard.balance().value(),
                    giftCard.countOfUses(),
                    giftCard.maxCountOfUses(),
                    giftCard.keyAndCounter().key(),
                    giftCard.keyAndCounter().counter(),
                    giftCard.creationDate(),
                    giftCard.expirationDate(),
                    giftCard.lastUsage(),
                    giftCard.version()));
    }

    @Override
    public Result<Integer, Throwable> update(GiftCard giftCard) {
        return mapTransactionResult(jet.write(UPDATE_GIFT_CARD,
                    giftCard.giftCardStatus(),
                    giftCard.balance().value(),
                    giftCard.countOfUses(),
                    giftCard.keyAndCounter().counter(),
                    giftCard.lastUsage(),
                    giftCard.version(),
                    giftCard.id(),
                    giftCard.previousVersion()));
    }

    @Override
    public Result<GiftCard, Throwable> findBy(CardID cardID) {
        var result = jet.read(FIND_BY_CARD_ID, this::mapGiftCard, cardID.value().toString());
        return new Result<>(result.value(), result.throwable(), result.success());
    }

    @Override
    public Result<List<GiftCard>, Throwable> findBy(BuyerID buyerID) {
        var result = jet.readListOf(FIND_BY_BUYER_ID, this::mapGiftCard, buyerID.value().toString());
        return new Result<>(result.value(), result.throwable(), result.success());
    }

    @Override
    public Result<List<GiftCard>, Throwable> findBy(OwnerID ownerID) {
        var result = jet.readListOf(FIND_BY_OWNER_ID, this::mapGiftCard, ownerID.value().toString());
        return new Result<>(result.value(), result.throwable(), result.success());
    }

    @Override
    public Result<List<GiftCard>, Throwable> findBy(StoreID storeID) {
        var result = jet.readListOf(FIND_BY_STORE_ID, this::mapGiftCard, storeID.value().toString());
        return new Result<>(result.value(), result.throwable(), result.success());
    }

    @Override
    public Result<List<CardDTO>, Throwable> availableGiftCards(Pageable page) {
        var result = jet.readListOf(FIND_ALL_AVAILABLE_CARDS, this::mapCardDTO, page.limit(), page.offset());
        return new Result<>(result.value(), result.throwable(), result.success());
    }

    private GiftCard mapGiftCard(ResultSet rs) throws SQLException {
        String ownerId = rs.getString("owner_id");
        String storeId = rs.getString("store_id");
        return GiftCard.fromRepository(
                CardID.fromString(rs.getString("id")),
                BuyerID.fromString(rs.getString("buyer_id")),
                ownerId != null ? OwnerID.fromString(ownerId) : null,
                storeId != null ? StoreID.fromString(storeId) : null,
                GiftCardStatus.valueOf(rs.getString("gift_card_status")),
                new Balance(rs.getBigDecimal("balance")),
                rs.getInt("count_of_uses"),
                rs.getInt("max_count_of_uses"),
                new KeyAndCounter(
                    rs.getString("secret_key"),
                    rs.getInt("counter")),
                rs.getTimestamp("creation_date").toLocalDateTime(),
                rs.getTimestamp("expiration_date").toLocalDateTime(),
                rs.getTimestamp("last_usage").toLocalDateTime(),
                rs.getLong("version"));
    }

    private CardDTO mapCardDTO(ResultSet rs) throws SQLException {
        return new CardDTO(UUID.fromString(rs.getString("id")),
                    rs.getInt("expiration_period_days"), rs.getInt("max_usage_count"));
    }
}
