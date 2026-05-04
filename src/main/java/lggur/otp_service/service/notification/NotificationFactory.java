package lggur.otp_service.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationFactory {

    private static final Logger log = LoggerFactory.getLogger(NotificationFactory.class);

    private final Map<String, NotificationService> services;

    public NotificationFactory(Map<String, NotificationService> services) {
        this.services = services;
    }

    public NotificationService get(String type) {
        NotificationService service = services.get(type.toUpperCase());

        if (service == null) {
            log.warn("Unsupported notification channel requested: {}", type);
            throw new RuntimeException("Unsupported channel: " + type);
        }

        log.debug("Resolved notification channel: {}", type.toUpperCase());
        return service;
    }
}
