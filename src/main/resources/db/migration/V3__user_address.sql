-- V3 (Leader) — bảng địa chỉ nhận hàng (1 user nhiều địa chỉ).
-- Tách bảng theo LUẬT chống phình (không nhồi cột địa chỉ vào users).
CREATE TABLE user_addresses (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id),
    receiver_name VARCHAR(120) NOT NULL,
    phone         VARCHAR(20) NOT NULL,
    address_line  VARCHAR(300) NOT NULL,
    ward          VARCHAR(120),
    district      VARCHAR(120),
    province      VARCHAR(120),
    is_default    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ
);
CREATE INDEX idx_user_addresses_user ON user_addresses(user_id);
