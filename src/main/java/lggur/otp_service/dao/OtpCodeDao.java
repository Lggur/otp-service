package lggur.otp_service.dao;

import lggur.otp_service.model.OtpCode;

import java.util.Optional;

public interface OtpCodeDao {
    OtpCode save(OtpCode code);

    Optional<OtpCode> findActiveCode(Long userId, String code, String operationId);

    void markAsUsed(Long id);

    int expireOldCodes();
}
