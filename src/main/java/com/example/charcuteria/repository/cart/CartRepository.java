package com.example.charcuteria.repository.cart;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer getProductQuantity(Integer productId, Integer userId) {
        String sql = "SELECT quantity FROM cart_items WHERE user_id = ? AND product_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, userId, productId);
    }

    public Integer addOneQuantity(Integer productId, Integer userId) {
        String sql = "UPDATE cart_items SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";

        return jdbcTemplate.update(sql, userId, productId);
    }

    public Integer addCartItem(Integer productId, Integer userId) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, 1)";

        return jdbcTemplate.update(sql, userId, productId);
    }

}
