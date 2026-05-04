package lggur.otp_service.service.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("EMAIL")
public class EmailNotificationService implements NotificationService {
    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String destination, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(destination);
        mail.setSubject("Your OTP Code");
        mail.setText(message);

        mailSender.send(mail);
    }
}
