package lggur.otp_service.service;

import lggur.otp_service.dao.OtpCodeDao;
import lggur.otp_service.dao.OtpConfigDao;
import lggur.otp_service.model.OtpCode;
import lggur.otp_service.model.OtpConfig;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
public class OtpService {
    private final OtpConfigDao configDao;
    private final OtpCodeDao codeDao;

    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpConfigDao configDao, OtpCodeDao codeDao) {
        this.configDao = configDao;
        this.codeDao = codeDao;
    }

    public OtpCode generate(Long userId, String operationId) {

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

        return codeDao.save(otp);
    }

    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

}

