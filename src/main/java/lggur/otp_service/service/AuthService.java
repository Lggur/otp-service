package lggur.otp_service.service;

import lggur.otp_service.dao.UserDao;
import lggur.otp_service.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(String username, String password, String role) {

        if ("ADMIN".equals(role) && userDao.countAdmins() > 0) {
            throw new RuntimeException("Admin already exists");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User();
        user.setUsername(username);
        user.setPassword(hash);
        user.setRole(role);

        return userDao.save(user);
    }

    public boolean login(String username, String password) {

        User user = userDao.findByUsername(username).orElseThrow();

        return BCrypt.checkpw(password, user.getPassword());
    }

}
