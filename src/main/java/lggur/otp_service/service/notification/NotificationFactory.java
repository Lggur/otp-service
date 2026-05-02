package lggur.otp_service.service.notification;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationFactory {

    private final Map<String, NotificationService> services;

    public NotificationFactory(Map<String, NotificationService> services) {
        this.services = services;
    }

    public NotificationService get(String type) {
        NotificationService service = services.get(type.toUpperCase());

        if (service == null) {
            throw new RuntimeException("Unsupported channel: " + type);
        }

        return service;
    }
}
