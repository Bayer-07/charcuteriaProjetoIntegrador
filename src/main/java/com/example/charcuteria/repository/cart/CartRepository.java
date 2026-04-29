package com.example.charcuteria.repository.cart;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.dto.cart.CartResponseDto;

@Repository
public class CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean getProductQuantity(Integer productId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM cart_items WHERE user_id = ? AND product_id = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productId);
        return count != null && count > 0;
    }

    public Integer addOneQuantity(Integer productId, Integer userId) {
        String sql = "UPDATE cart_items SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";

        return jdbcTemplate.update(sql, userId, productId);
    }

    public Integer addCartItem(Integer productId, Integer userId) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, 1)";

        return jdbcTemplate.update(sql, userId, productId);
    }

    public List<CartResponseDto> findAllByUserId(Integer userId) {
        String sql = "SELECT ci.id, ci.quantity, ci.product_id, p.name, p.price, p.image_path FROM cart_items ci INNER JOIN products p ON ci.product_id = p.id WHERE ci.user_id = ? ORDER BY id";

        return jdbcTemplate.query(sql,
        (rs, rowNum) -> new CartResponseDto(
            rs.getInt("id"),
            rs.getInt("product_id"),
            rs.getString("name"),
            rs.getString("image_path"),
            rs.getBigDecimal("price"),
            rs.getInt("quantity")
        ), userId);
    }

    public Integer getCurrentlyQuantity(Integer itemId) {
        String sql = "SELECT quantity FROM cart_items WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, itemId);
    }

    public void updateCartQuantity(Integer itemId, Integer quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        jdbcTemplate.update(sql, quantity, itemId);
    }

    public Boolean deleteProductFromCart(Integer cartItemId) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, cartItemId);
        return rowsAffected > 0;
    }
}
