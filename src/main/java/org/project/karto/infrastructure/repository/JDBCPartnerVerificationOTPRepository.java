package org.project.karto.infrastructure.repository;

import com.hadzhy.jetquerious.jdbc.JetQuerious;
import com.hadzhy.jetquerious.sql.QueryForge;
import jakarta.enterprise.context.ApplicationScoped;
import org.project.karto.domain.common.containers.Result;
import org.project.karto.domain.companies.entities.PartnerVerificationOTP;
import org.project.karto.domain.companies.repository.PartnerVerificationOTPRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static com.hadzhy.jetquerious.sql.QueryForge.*;
import static org.project.karto.infrastructure.repository.JDBCCompanyRepository.mapTransactionResult;

@ApplicationScoped
public class JDBCPartnerVerificationOTPRepository implements PartnerVerificationOTPRepository {

    private final JetQuerious jet;

    static final String SAVE_PARTNER_OTP = insert()
            .into("companies_otp")
            .column("otp")
            .column("company_id")
            .column("is_confirmed")
            .column("creation_date")
            .column("expiration_date")
            .values()
            .build()
            .sql();

    static final String UPDATE = QueryForge.update("companies_otp")
            .set("is_confirmed = ?")
            .where("otp = ?")
            .build()
            .sql();

    static final String REMOVE = delete()
            .from("companies_otp")
            .where("otp = ?")
            .build()
            .sql();

    static final String UPDATE_CONFIRMATION = QueryForge.update("companies_otp")
            .set("is_confirmed = ?")
            .where("otp = ?")
            .build()
            .sql();

    static final String FIND_BY_OTP = select()
            .all()
            .from("companies_otp")
            .where("otp = ?")
            .build()
            .sql();

    static final String FIND_BY_COMPANY_ID = select()
            .all()
            .from("companies_otp")
            .where("company_id = ?")
            .build()
            .sql();

    JDBCPartnerVerificationOTPRepository() {
        this.jet = JetQuerious.instance();
    }

    @Override
    public Result<Integer, Throwable> save(PartnerVerificationOTP otp) {
        return mapTransactionResult(
                jet.write(SAVE_PARTNER_OTP, otp.otp(), otp.companyID(), otp.isConfirmed(), otp.creationDate(), otp.expirationDate())
        );
    }

    @Override
    public Result<Integer, Throwable> remove(PartnerVerificationOTP otp) {
        return mapTransactionResult(jet.write(REMOVE, otp.otp()));
    }

    @Override
    public Result<Integer, Throwable> updateConfirmation(PartnerVerificationOTP otp) {
        return mapTransactionResult(jet.write(UPDATE_CONFIRMATION, otp.isConfirmed(), otp.otp()));
    }

    @Override
    public Result<PartnerVerificationOTP, Throwable> findBy(PartnerVerificationOTP otp) {
        var result = jet.read(FIND_BY_OTP, this::partnerOTPMapper, otp.otp());
        return mapResult(result);
    }

    @Override
    public Result<PartnerVerificationOTP, Throwable> findBy(UUID companyID) {
        return mapResult(jet.read(FIND_BY_COMPANY_ID, this::partnerOTPMapper, companyID));
    }

    @Override
    public Result<PartnerVerificationOTP, Throwable> findBy(String otp) {
        var result = jet.read(FIND_BY_OTP, this::partnerOTPMapper, otp);
        return mapResult(result);
    }

    private PartnerVerificationOTP partnerOTPMapper(ResultSet rs) throws SQLException {
        return PartnerVerificationOTP.fromRepository(
                rs.getString("otp"),
                UUID.fromString(rs.getString("company_id")),
                rs.getBoolean("is_confirmed"),
                rs.getObject("creation_date", Timestamp.class).toLocalDateTime(),
                rs.getObject("expiration_date", Timestamp.class).toLocalDateTime()
        );
    }

    private Result<PartnerVerificationOTP, Throwable> mapResult(
            com.hadzhy.jetquerious.util.Result<PartnerVerificationOTP, Throwable> result
    ) {
        return new Result<>(result.value(), result.throwable(), result.success());
    }
}
