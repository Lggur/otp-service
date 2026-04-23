package lggur.otp_service.api;

import lggur.otp_service.model.User;
import lggur.otp_service.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
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
        return authService.register(username, password, role);
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        boolean ok = authService.login(username, password);

        return ok ? "OK" : "FAIL";
    }
}
