package lggur.otp_service.api;

import lggur.otp_service.model.User;
import lggur.otp_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(defaultValue = "USER") String role
    ) {
        log.info("Registration request for user: {}, role: {}", username, role);
        User user = authService.register(username, password, role);
        log.info("User registered successfully: id={}, username={}", user.getId(), user.getUsername());
        return user;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        log.info("Login attempt for user: {}", username);
        String token = authService.login(username, password);
        log.info("Login successful for user: {}", username);
        return token;
    }


}
