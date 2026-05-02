package lggur.otp_service.service.notification;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service("FILE")
public class FileNotificationService implements NotificationService{
    public void send(String destination, String message) {
        try (FileWriter fw = new FileWriter("otp.log", true)) {
            fw.write("TO=" + destination + " MESSAGE=" + message + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
