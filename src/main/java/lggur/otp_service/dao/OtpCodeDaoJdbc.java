package lggur.otp_service.dao;

import lggur.otp_service.model.OtpCode;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

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

    @Override
    public Optional<OtpCode> findActiveCode(Long userId, String code, String operationId) {
        String sql = """
                SELECT * FROM otp_codes
                WHERE user_id = ?
                  AND code = ?
                  AND status = 'ACTIVE'
                  AND (operation_id = ? OR ? IS NULL)
                LIMIT 1
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, code);
            preparedStatement.setString(3, operationId);
            preparedStatement.setString(4, operationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                OtpCode otp = new OtpCode();
                otp.setId(resultSet.getLong("id"));
                otp.setUserId(resultSet.getLong("user_id"));
                otp.setCode(resultSet.getString("code"));
                otp.setStatus(resultSet.getString("status"));
                otp.setExpiresAt(resultSet.getObject("expires_at", java.time.OffsetDateTime.class));
                return Optional.of(otp);
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markAsUsed(Long id) {
        String sql = """
                UPDATE otp_codes
                SET status = 'USED',
                    used_at = NOW()
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int expireOldCodes() {
        String sql = """
                UPDATE otp_codes
                SET status = 'EXPIRED'
                WHERE status = 'ACTIVE'
                  AND expires_at <= NOW()
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validateAndMarkUsed(Long userId, String code, String operationId) {
        String sql = """
                UPDATE otp_codes
                SET status = 'USED',
                    used_at = NOW()
                WHERE id = (
                    SELECT id FROM otp_codes
                    WHERE user_id = ?
                      AND code = ?
                      AND status = 'ACTIVE'
                      AND expires_at > NOW()
                      AND (operation_id = ? OR ? IS NULL)
                    ORDER BY created_at DESC
                    LIMIT 1
                )
                RETURNING id
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, code);
            preparedStatement.setString(3, operationId);
            preparedStatement.setString(4, operationId);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}



