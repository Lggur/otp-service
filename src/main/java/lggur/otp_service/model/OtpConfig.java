package lggur.otp_service.model;

import java.time.OffsetDateTime;

public class OtpConfig {
    private int id;
    private int codeLength;
    private int ttlSeconds;
    private OffsetDateTime updatedAt;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setTtlSeconds(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
