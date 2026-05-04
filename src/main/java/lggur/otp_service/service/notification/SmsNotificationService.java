package lggur.otp_service.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("SMS")
public class SmsNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public void send(String destination, String message) {
        log.info("[SMS] Sending to={}, msg={}", destination, message);
    }
}
