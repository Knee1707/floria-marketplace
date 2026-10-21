-- V8 — Đánh giá cửa hàng & shipper sau khi đơn giao thành công (F3.18).
-- Mỗi đơn chỉ 1 đánh giá cho mỗi đối tượng (SHOP / SHIPPER) từ 1 người mua.
CREATE TABLE order_reviews (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    target     VARCHAR(16) NOT NULL,          -- SHOP | SHIPPER
    rating     INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    UNIQUE (order_id, user_id, target)
);

CREATE INDEX idx_order_reviews_order ON order_reviews(order_id);
