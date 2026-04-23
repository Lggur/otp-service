DROP TABLE IF EXISTS otp_codes CASCADE;
DROP TABLE IF EXISTS otp_config CASCADE;
DROP TABLE IF EXISTS users CASCADE;

DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS otp_status CASCADE;


CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE otp_status AS ENUM ('ACTIVE', 'EXPIRED', 'USED');

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role     ON users(role);


CREATE TABLE otp_config (
     id INT PRIMARY KEY DEFAULT 1,
     code_length INT NOT NULL DEFAULT 6,
     ttl_seconds INT NOT NULL DEFAULT 300,
     updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

     CONSTRAINT chk_otp_config_singleton CHECK (id = 1),
     CONSTRAINT chk_code_length CHECK (code_length BETWEEN 4 AND 12),
     CONSTRAINT chk_ttl_seconds CHECK (ttl_seconds > 0)
 );

INSERT INTO otp_config(id, code_length, ttl_seconds)
VALUES (1, 6, 300);


CREATE TABLE otp_codes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code VARCHAR(12) NOT NULL,
    status otp_status NOT NULL DEFAULT 'ACTIVE',
    operation_id VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ
);

CREATE INDEX idx_otp_codes_user_id ON otp_codes(user_id);
CREATE INDEX idx_otp_codes_status ON otp_codes(status);
CREATE INDEX idx_otp_codes_expires_at ON otp_codes(expires_at) WHERE status = 'ACTIVE';
CREATE INDEX idx_otp_codes_operation ON otp_codes(operation_id) WHERE operation_id IS NOT NULL;


CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_otp_config_updated_at
    BEFORE UPDATE ON otp_config
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();