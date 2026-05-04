package lggur.otp_service.service;

import lggur.otp_service.dao.OtpCodeDao;
import lggur.otp_service.dao.OtpConfigDao;
import lggur.otp_service.model.OtpCode;
import lggur.otp_service.model.OtpConfig;
import lggur.otp_service.service.notification.NotificationFactory;
import lggur.otp_service.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final OtpConfigDao configDao;
    private final OtpCodeDao codeDao;
    private final NotificationFactory notificationFactory;

    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpConfigDao configDao, OtpCodeDao codeDao, NotificationFactory notificationFactory) {
        this.configDao = configDao;
        this.codeDao = codeDao;
        this.notificationFactory = notificationFactory;
    }

    public OtpCode generate(Long userId, String operationId, String channel, String destination) {
        log.info("Generating OTP: userId={}, channel={}, operationId={}", userId, channel, operationId);

        OtpConfig config = configDao.getConfig();
        log.debug("OTP config loaded: codeLength={}, ttlSeconds={}", config.getCodeLength(), config.getTtlSeconds());

        String code = generateCode(config.getCodeLength());

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusSeconds(config.getTtlSeconds());

        OtpCode otp = new OtpCode();
        otp.setUserId(userId);
        otp.setCode(code);
        otp.setStatus("ACTIVE");
        otp.setOperationId(operationId);
        otp.setExpiresAt(expiresAt);

        codeDao.save(otp);
        log.debug("OTP code saved: otpId={}, expiresAt={}", otp.getId(), expiresAt);

        NotificationService notificationService = notificationFactory.get(channel);

        notificationService.send(destination, "Your OTP code: " + code);
        log.info("OTP notification sent via {}: userId={}", channel, userId);

        return codeDao.save(otp);
    }

    private String generateCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }

        return stringBuilder.toString();
    }

    public boolean validate(Long userId, String code, String operationId) {
        log.info("Validating OTP: userId={}, operationId={}", userId, operationId);
        boolean result = codeDao.validateAndMarkUsed(userId, code, operationId);
        log.info("OTP validation result: userId={}, valid={}", userId, result);
        return result;
    }

}
