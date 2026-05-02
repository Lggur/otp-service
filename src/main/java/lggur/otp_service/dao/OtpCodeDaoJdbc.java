package lggur.otp_service.dao;

import lggur.otp_service.model.OtpCode;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class OtpCodeDaoJdbc implements OtpCodeDao {
    private final DataSource dataSource;

    public OtpCodeDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public OtpCode save(OtpCode code) {

        String sql = """
            INSERT INTO otp_codes(user_id, code, status, operation_id, expires_at)
            VALUES (?, ?, ?::otp_status, ?, ?)
            RETURNING id
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, code.getUserId());
            preparedStatement.setString(2, code.getCode());
            preparedStatement.setString(3, code.getStatus());
            preparedStatement.setString(4, code.getOperationId());
            preparedStatement.setObject(5, code.getExpiresAt());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                code.setId(resultSet.getLong(1));
            }

            return code;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}



