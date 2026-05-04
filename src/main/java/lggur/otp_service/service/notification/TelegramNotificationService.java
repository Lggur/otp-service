package lggur.otp_service.service.notification;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service("TELEGRAM")
public class TelegramNotificationService implements NotificationService {
    String token = "8768480144:AAEgpJvTJHYEUkbMrxaiMUPk_aViKLpfUAg";

    @Override
    public void send(String chatId, String message) {

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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
