package lggur.otp_service.dao;

import lggur.otp_service.model.OtpConfig;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Repository
public class OtpConfigDaoJdbc implements OtpConfigDao {
    private final DataSource dataSource;

    public OtpConfigDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public OtpConfig getConfig() {
        String sql = "SELECT * FROM otp_config WHERE id = 1";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                OtpConfig config = new OtpConfig();
                config.setId(resultSet.getInt("id"));
                config.setCodeLength(resultSet.getInt("code_length"));
                config.setTtlSeconds(resultSet.getInt("ttl_seconds"));
                return config;
            }

            throw new RuntimeException("OTP config not found");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
