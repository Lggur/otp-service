package lggur.otp_service.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service("TELEGRAM")
public class TelegramNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final String token;

    public TelegramNotificationService(@Value("${telegram.bot.token}") String token) {
        this.token = token;
    }

    @Override
    public void send(String chatId, String message) {
        log.info("Sending Telegram message to chatId={}", chatId);

        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    token,
                    chatId,
                    encodedMessage
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            log.debug("Telegram message sent successfully to chatId={}", chatId);

        } catch (Exception e) {
            log.error("Failed to send Telegram message to chatId={}: {}", chatId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
