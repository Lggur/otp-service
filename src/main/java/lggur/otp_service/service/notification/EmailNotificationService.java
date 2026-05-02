package lggur.otp_service.service.notification;

import org.springframework.stereotype.Service;

@Service("EMAIL")
    public class EmailNotificationService implements NotificationService {

        @Override
        public void send(String destination, String message) {
            System.out.println("[EMAIL] to=" + destination + " msg=" + message);
        }
    }
