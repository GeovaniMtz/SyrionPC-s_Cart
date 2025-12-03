-- TABLA 1: category
CREATE TABLE category(
    category_id INT NOT NULL AUTO_INCREMENT,
    category VARCHAR(100) NOT NULL,
    tag VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL,

    PRIMARY KEY (category_id),
    CHECK (status IN (0,1))
);

CREATE UNIQUE INDEX ux_category ON category(category);
CREATE UNIQUE INDEX ux_tag ON category(tag);

-- Datos de ejemplo para 'category'
INSERT INTO category (category, tag, status)
VALUES
  ('Procesadores Intel y AMD', 'cpu', 1),
  ('Tarjetas Gráficas NVIDIA/AMD', 'gpu', 1),
  ('Laptops', 'lap', 1),
  ('Modems', 'md', 0);

---
-- TABLA 2: product
CREATE TABLE product (
    product_id INT NOT NULL AUTO_INCREMENT,
    gtin VARCHAR(13) NOT NULL,
    product VARCHAR(100) NOT NULL,
    description TEXT,
    price FLOAT NOT NULL,
    stock INT NOT NULL,
    category_id INT NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,

    PRIMARY KEY (product_id),
    UNIQUE KEY ux_product_gtin (gtin),
    UNIQUE KEY ux_product_product (product),
    FOREIGN KEY fk_product_category (category_id) REFERENCES category(category_id),
    CHECK (status IN (0,1))
);

-- Tabla 'product_image'
CREATE TABLE product_image (
    product_image_id INT NOT NULL AUTO_INCREMENT,
    product_id INT NOT NULL,
    image VARCHAR(255) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,

    PRIMARY KEY (product_image_id),
    FOREIGN KEY fk_product_image (product_id) REFERENCES product(product_id),
    CHECK (status IN (0,1))
);

---
-- TABLA 3: cart_item
CREATE TABLE cart_item (
    id INT NOT NULL AUTO_INCREMENT,
    client_id VARCHAR(100) NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- INDICES
CREATE UNIQUE INDEX ux_cart_item_client_product
ON cart_item (client_id, product_id);

CREATE INDEX ix_cart_item_client
ON cart_item (client_id);