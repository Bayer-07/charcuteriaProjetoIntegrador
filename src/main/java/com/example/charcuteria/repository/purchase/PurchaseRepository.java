package com.example.charcuteria.repository.purchase;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PurchaseRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<CartItemDb> fetchCartItemsWithPricesAndStock(Integer userId) {
        String sql = """
            SELECT ci.product_id, p.name, ci.quantity as cart_qty, p.price, p.stock_quantity
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            WHERE ci.user_id = ?
            FOR UPDATE OF p
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CartItemDb(
            rs.getInt("product_id"),
            rs.getString("name"),
            rs.getInt("cart_qty"),
            rs.getBigDecimal("price"),
            rs.getInt("stock_quantity")
        ), userId);
    }

    public Optional<String> fetchUserZipCode(Long addressId, Integer userId) {
        String sql = "SELECT zip_code FROM addresses WHERE id = ? AND user_id = ?";
        try {
            String zipCode = jdbcTemplate.queryForObject(sql, String.class, addressId, userId);
            return Optional.ofNullable(zipCode);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Integer createOrder(Integer userId, Long addressId, BigDecimal totalAmount, BigDecimal shippingCost) {
        String sql = "INSERT INTO orders (user_id, address_id, total_amount, shipping_cost, status) VALUES (?, ?, ?, ?, 'PENDING')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, userId);
            ps.setLong(2, addressId);
            ps.setBigDecimal(3, totalAmount);
            ps.setBigDecimal(4, shippingCost);
            return ps;
        }, keyHolder);

        Number key = (Number) keyHolder.getKeys().get("id");
        if (key == null) {
            throw new IllegalStateException("Falha ao gerar ID do pedido no banco de dados.");
        }
        return key.intValue();
    }

    public void insertOrderProducts(Integer orderId, List<CartItemDb> items) {
        String sql = "INSERT INTO order_products (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, items, items.size(), (ps, item) -> {
            ps.setInt(1, orderId);
            ps.setInt(2, item.productId());
            ps.setInt(3, item.quantity());
            ps.setBigDecimal(4, item.price());
        });
    }

    public void clearCart(Integer userId) {
        jdbcTemplate.update("DELETE FROM cart_items WHERE user_id = ?", userId);
    }

    public void deductStock(List<CartItemDb> items) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?";
        jdbcTemplate.batchUpdate(sql, items, items.size(), (ps, item) -> {
            ps.setInt(1, item.quantity());
            ps.setInt(2, item.productId());
        });
    }

    public record CartItemDb(Integer productId, String productName, Integer quantity, BigDecimal price, Integer stockQuantity) {}
}
