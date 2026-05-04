package lggur.otp_service.service;

import lggur.otp_service.dao.OtpCodeDao;
import lggur.otp_service.dao.OtpConfigDao;
import lggur.otp_service.model.OtpCode;
import lggur.otp_service.model.OtpConfig;
import lggur.otp_service.service.notification.NotificationFactory;
import lggur.otp_service.service.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
public class OtpService {
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

        OtpConfig config = configDao.getConfig();

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

        NotificationService notificationService = notificationFactory.get(channel);

        notificationService.send(destination, "Your OTP code: " + code);

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
        return codeDao.validateAndMarkUsed(userId, code, operationId);
    }

}

