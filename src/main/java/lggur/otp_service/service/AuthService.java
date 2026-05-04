package lggur.otp_service.service;

import lggur.otp_service.dao.UserDao;
import lggur.otp_service.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserDao userDao;
    private final JwtService jwtService;

    public AuthService(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public User register(String username, String password, String role) {
        log.debug("Registering user: username={}, role={}", username, role);

        if ("ADMIN".equals(role) && userDao.countAdmins() > 0) {
            log.warn("Admin registration rejected: admin already exists");
            throw new RuntimeException("Admin already exists");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User();
        user.setUsername(username);
        user.setPassword(hash);
        user.setRole(role);

        User saved = userDao.save(user);
        log.info("User registered: id={}, username={}, role={}", saved.getId(), saved.getUsername(), saved.getRole());
        return saved;
    }

    public String login(String username, String password) {
        log.debug("Login attempt: username={}", username);

        User user = userDao.findByUsername(username).orElseThrow();

        if (!BCrypt.checkpw(password, user.getPassword())) {
            log.warn("Login failed: invalid credentials for user={}", username);
            throw new RuntimeException("Invalid credentials");
        }

        log.info("Login successful: userId={}, role={}", user.getId(), user.getRole());
        return jwtService.generateToken(user.getId(), user.getRole());
    }

}
