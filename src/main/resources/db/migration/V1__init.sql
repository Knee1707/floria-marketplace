-- ============================================================
-- StarShop schema V1 - 26 bảng. Flyway là NGUỒN SCHEMA DUY NHẤT.
-- Quy ước: id bigserial PK, created_at/updated_at timestamptz.
-- Thành viên đổi schema -> tạo file V{n}__*.sql MỚI, KHÔNG sửa file này.
-- ============================================================

-- ---------- Nhóm cấp độ & tài khoản ----------
CREATE TABLE user_levels (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(60) NOT NULL,
    discount_percent NUMERIC(5,2) NOT NULL DEFAULT 0,
    threshold        BIGINT,
    created_at       TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ
);

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(20) UNIQUE,
    password      VARCHAR(255) NOT NULL,
    full_name     VARCHAR(120),
    avatar_url    VARCHAR(500),
    role          VARCHAR(20) NOT NULL,
    enabled       BOOLEAN NOT NULL DEFAULT FALSE,
    locked        BOOLEAN NOT NULL DEFAULT FALSE,
    user_level_id BIGINT REFERENCES user_levels(id),
    created_at    TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ
);

CREATE TABLE otp_tokens (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(150) NOT NULL,
    code       VARCHAR(10) NOT NULL,
    purpose    VARCHAR(20) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

-- ---------- Nhóm cửa hàng ----------
CREATE TABLE store_levels (
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(60) NOT NULL,
    commission_rate  NUMERIC(5,2) NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ
);

CREATE TABLE stores (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(150) NOT NULL,
    description    TEXT,
    avatar_url     VARCHAR(500),
    cover_url      VARCHAR(500),
    owner_id       BIGINT NOT NULL REFERENCES users(id),
    store_level_id BIGINT REFERENCES store_levels(id),
    approved       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ,
    updated_at     TIMESTAMPTZ
);

-- Manager phụ trách shop (tuỳ chọn - cho role MANAGER)
CREATE TABLE store_managers (
    id         BIGSERIAL PRIMARY KEY,
    store_id   BIGINT NOT NULL REFERENCES stores(id),
    manager_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    UNIQUE (store_id, manager_id)
);

CREATE TABLE commissions (
    id          BIGSERIAL PRIMARY KEY,
    store_id    BIGINT REFERENCES stores(id),
    rate        NUMERIC(5,2) NOT NULL,
    note        VARCHAR(255),
    created_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ
);

-- ---------- Nhóm danh mục & sản phẩm ----------
CREATE TABLE categories (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(120) NOT NULL,
    image_url  VARCHAR(500),
    parent_id  BIGINT REFERENCES categories(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE styles (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(80) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE style_values (
    id         BIGSERIAL PRIMARY KEY,
    style_id   BIGINT NOT NULL REFERENCES styles(id),
    value      VARCHAR(80) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE products (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(200) NOT NULL,
    description   TEXT,
    price         NUMERIC(12,2) NOT NULL,
    stock         INTEGER NOT NULL DEFAULT 0,
    thumbnail_url VARCHAR(500),
    sold_count    BIGINT NOT NULL DEFAULT 0,
    rating_avg    NUMERIC(3,2) NOT NULL DEFAULT 0,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    store_id      BIGINT NOT NULL REFERENCES stores(id),
    category_id   BIGINT REFERENCES categories(id),
    created_at    TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ
);

-- ---------- Nhóm vận chuyển ----------
CREATE TABLE deliveries (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(120) NOT NULL,
    shipping_fee  NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ
);

-- ---------- Nhóm theo dõi & đánh giá ----------
CREATE TABLE user_follow_store (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    store_id   BIGINT NOT NULL REFERENCES stores(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    UNIQUE (user_id, store_id)
);

CREATE TABLE user_follow_product (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    UNIQUE (user_id, product_id)
);

CREATE TABLE product_view_history (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    viewed_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE reviews (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    user_id    BIGINT NOT NULL REFERENCES users(id),
    rating     INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE review_media (
    id         BIGSERIAL PRIMARY KEY,
    review_id  BIGINT NOT NULL REFERENCES reviews(id),
    media_url  VARCHAR(500) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

-- ---------- Nhóm giỏ & đơn ----------
CREATE TABLE carts (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE REFERENCES users(id),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE cart_items (
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT NOT NULL REFERENCES carts(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity   INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE orders (
    id               BIGSERIAL PRIMARY KEY,
    code             VARCHAR(30) NOT NULL UNIQUE,
    buyer_id         BIGINT NOT NULL REFERENCES users(id),
    store_id         BIGINT NOT NULL REFERENCES stores(id),
    shipper_id       BIGINT REFERENCES users(id),
    status           VARCHAR(20) NOT NULL DEFAULT 'NEW',
    payment_method   VARCHAR(20) NOT NULL,
    paid             BOOLEAN NOT NULL DEFAULT FALSE,
    subtotal         NUMERIC(12,2) NOT NULL DEFAULT 0,
    shipping_fee     NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount         NUMERIC(12,2) NOT NULL DEFAULT 0,
    total            NUMERIC(12,2) NOT NULL DEFAULT 0,
    shipping_address VARCHAR(300),
    created_at       TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ
);

CREATE TABLE order_items (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity   INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

-- ---------- Nhóm khuyến mãi ----------
CREATE TABLE promotions (
    id         BIGSERIAL PRIMARY KEY,
    store_id   BIGINT REFERENCES stores(id),
    type       VARCHAR(20) NOT NULL,
    value      NUMERIC(12,2) NOT NULL,
    start_at   TIMESTAMPTZ,
    end_at     TIMESTAMPTZ,
    quota      INTEGER,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE vouchers (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(40) NOT NULL UNIQUE,
    type         VARCHAR(20) NOT NULL,
    value        NUMERIC(12,2) NOT NULL,
    min_order    NUMERIC(12,2),
    max_discount NUMERIC(12,2),
    quota        INTEGER,
    used_count   INTEGER DEFAULT 0,
    start_at     TIMESTAMPTZ,
    end_at       TIMESTAMPTZ,
    store_id     BIGINT REFERENCES stores(id),
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ
);

CREATE TABLE order_vouchers (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT NOT NULL REFERENCES orders(id),
    voucher_id      BIGINT NOT NULL REFERENCES vouchers(id),
    discount_amount NUMERIC(12,2) NOT NULL,
    created_at      TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ
);

-- ---------- Nhóm tài chính & giao ----------
CREATE TABLE transactions (
    id         BIGSERIAL PRIMARY KEY,
    store_id   BIGINT REFERENCES stores(id),
    order_id   BIGINT REFERENCES orders(id),
    type       VARCHAR(20) NOT NULL,
    amount     NUMERIC(12,2) NOT NULL,
    note       VARCHAR(255),
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE delivery_assignments (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES orders(id),
    shipper_id   BIGINT NOT NULL REFERENCES users(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED',
    assigned_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    delivered_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ,
    updated_at   TIMESTAMPTZ
);

-- ---------- Nhóm thông báo ----------
CREATE TABLE notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    type       VARCHAR(20) NOT NULL,
    content    VARCHAR(500) NOT NULL,
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

-- ---------- Index gợi ý ----------
CREATE INDEX idx_products_store   ON products(store_id);
CREATE INDEX idx_products_status  ON products(status);
CREATE INDEX idx_orders_buyer     ON orders(buyer_id);
CREATE INDEX idx_orders_store     ON orders(store_id);
CREATE INDEX idx_orders_shipper   ON orders(shipper_id);
