package lggur.otp_service.model;

import java.time.OffsetDateTime;

public class OtpCode {
    private Long id;
    private Long userId;
    private String code;
    private String status;
    private String operationId;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
    private OffsetDateTime usedAt;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setUsedAt(OffsetDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public OffsetDateTime getUsedAt() {
        return usedAt;
    }
}
