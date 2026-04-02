INSERT INTO categories (name, description) VALUES
('Embutidos', 'Produtos ensacados e curados'),
('Defumados', 'Carnes expostas à fumaça de lenha'),
('Kits', 'Kits prontos para presente');

INSERT INTO subscription_plans (name, description, price) VALUES
('Plano Mensal Premium', 'Receba 3 produtos surpresa todo mês', 89.90),
('Clube do Salame', 'Seleção de salames artesanais', 55.00);

INSERT INTO users (name, email, password_hash, role) VALUES
('Admin Teste', 'admin@koch.com', 'hash_seguro', 'ADMIN'),
('Cliente Teste', 'cliente@gmail.com', 'hash_seguro', 'CUSTOMER');

INSERT INTO addresses (user_id, street, number, neighborhood, city, state, zip_code, is_default) VALUES
(2, 'Rua das Flores', '123', 'Centro', 'Toledo', 'PR', '85900-000', TRUE);

INSERT INTO products (category_id, name, description, price, stock_quantity, is_active) VALUES
(1, 'Salame Italiano', 'Curado por 30 dias', 45.90, 50, TRUE),
(2, 'Bacon Defumado', 'Defumação artesanal', 35.50, 100, TRUE);

INSERT INTO cart_items (user_id, product_id, quantity) VALUES (2, 2, 3);

INSERT INTO subscriptions (user_id, plan_id, status) VALUES (2, 1, 'ACTIVE');
