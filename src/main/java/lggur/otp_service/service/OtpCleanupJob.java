package lggur.otp_service.service;

import lggur.otp_service.dao.OtpCodeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OtpCleanupJob {
    private static final Logger log = LoggerFactory.getLogger(OtpCleanupJob.class);

    private final OtpCodeDao otpCodeDao;

    public OtpCleanupJob(OtpCodeDao otpCodeDao) {
        this.otpCodeDao = otpCodeDao;
    }

    @Scheduled(fixedRate = 60000)
    public void expireCodes() {
        int updated = otpCodeDao.expireOldCodes();

        if (updated > 0) {
            log.info("Expired OTP codes: {}", updated);
        }
    }
}
