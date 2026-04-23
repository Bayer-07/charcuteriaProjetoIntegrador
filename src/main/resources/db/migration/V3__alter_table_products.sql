ALTER TABLE products
ALTER COLUMN category_id DROP NOT NULL;

ALTER TABLE products
DROP CONSTRAINT fk_product_category;

ALTER TABLE products
ADD CONSTRAINT fk_product_category
FOREIGN KEY (category_id)
REFERENCES categories(id)
ON DELETE SET NULL;
