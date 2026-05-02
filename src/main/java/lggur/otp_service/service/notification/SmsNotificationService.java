package lggur.otp_service.service.notification;

import org.springframework.stereotype.Service;

@Service("SMS")
public class SmsNotificationService implements NotificationService {

    @Override
    public void send(String destination, String message) {
        System.out.println("[SMS] to=" + destination + " msg=" + message);
    }
}
