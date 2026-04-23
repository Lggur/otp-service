package lggur.otp_service.dao;


import lggur.otp_service.model.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findByUsername(String username);

    User save(User user);

    long countAdmins();
}
