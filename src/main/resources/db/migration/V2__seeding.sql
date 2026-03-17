INSERT INTO users (name, email, password_hash, role) VALUES
('teste Admin', 'admin@koch.com', 'hash_seguro_admin', 'ADMIN'),
('Cliente Teste', 'cliente@gmail.com', 'hash_seguro_cliente', 'CUSTOMER');

INSERT INTO addresses (user_id, street, number, neighborhood, city, state, zip_code, is_default) VALUES
(2, 'Rua das Flores', '123', 'Centro', 'Toledo', 'PR', '85900-000', TRUE),
(2, 'Av. Maripá', '456', 'Vila Industrial', 'Toledo', 'PR', '85905-000', FALSE);

INSERT INTO products (name, description, price, stock_quantity, image_path, is_active) VALUES
('Salame Italiano', 'Salame artesanal curado por 30 dias', 45.90, 50, '/uploads/salame.jpg', TRUE),
('Copa Lombo', 'Copa maturada com especiarias finas', 62.00, 30, '/uploads/copa.jpg', TRUE),
('Bacon Defumado', 'Bacon defumado em lenha de macieira', 35.50, 100, '/uploads/bacon.jpg', TRUE),
('Linguiça Suína', 'Linguiça caseira para churrasco', 28.00, 0, '/uploads/linguica.jpg', TRUE);

INSERT INTO orders (user_id, address_id, total_amount, shipping_cost, status) VALUES
(2, 1, 153.80, 15.00, 'PAID');

INSERT INTO order_products (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 2, 45.90);
(1, 2, 1, 62.00);

INSERT INTO subscription (user_id, plan_type, status) VALUES
(2, 'Plano Mensal Premium', 'ACTIVE');
