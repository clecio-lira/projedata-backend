CREATE TABLE raw_materials (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    name VARCHAR(255),
    stock_quantity INTEGER
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    name VARCHAR(255),
    price NUMERIC(15,2)
);

CREATE TABLE product_raw_materials (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT,
    raw_material_id BIGINT,
    quantity_needed INTEGER,

    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_raw_material
        FOREIGN KEY (raw_material_id)
        REFERENCES raw_materials (id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_product_raw_material
        UNIQUE (product_id, raw_material_id)
);