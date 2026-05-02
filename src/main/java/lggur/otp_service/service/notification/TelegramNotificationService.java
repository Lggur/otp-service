package lggur.otp_service.service.notification;

import org.springframework.stereotype.Service;

@Service("TELEGRAM")
public class TelegramNotificationService implements NotificationService {

    @Override
    public void send(String destination, String message) {
        System.out.println("[TELEGRAM] to=" + destination + " msg=" + message);
    }
}
