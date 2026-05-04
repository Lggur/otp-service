package lggur.otp_service.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service("FILE")
public class FileNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(FileNotificationService.class);

    public void send(String destination, String message) {
        log.info("Writing OTP to file for destination={}", destination);
        try (FileWriter fw = new FileWriter("otp.log", true)) {
            fw.write("TO=" + destination + " MESSAGE=" + message + "\n");
            log.debug("OTP written to otp.log successfully");
        } catch (IOException e) {
            log.error("Failed to write OTP to file: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
