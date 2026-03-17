-- Por favor se voce nao sabe o por que esta aqui nao rode isso

-- DELETE tables (V1)
DROP TABLE users CASCADE;
DROP TABLE addresses CASCADE;
DROP TABLE orders CASCADE;
DROP TABLE order_products CASCADE;
DROP TABLE products CASCADE;
DROP TABLE subscription CASCADE;

-- TRUNKATE tables (V2)
DELETE FROM order_products WHERE order_id = 1;
DELETE FROM orders WHERE id = 1;
DELETE FROM subscription WHERE user_id = 2;
DELETE FROM addresses WHERE user_id = 2;
DELETE FROM products WHERE name IN ('Salame Italiano', 'Copa Lombo', 'Bacon Defumado', 'Linguiça Suína');
DELETE FROM users WHERE id IN (1, 2);
