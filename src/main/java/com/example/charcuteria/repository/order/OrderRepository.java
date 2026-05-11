package com.example.charcuteria.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.model.Order;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> findByUserId(Integer userId) {
        String sql = "SELECT id, user_id, address_id, total_amount, shipping_cost, status, order_date FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setAddressId(rs.getInt("address_id"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));
            order.setShippingCost(rs.getBigDecimal("shipping_cost"));
            order.setStatus(rs.getString("status"));
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            return order;
        }, userId);
    }

    public Optional<Order> findById(Integer id) {
        String sql = "SELECT id, user_id, address_id, total_amount, shipping_cost, status, order_date FROM orders WHERE id = ?";

        List<Order> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setAddressId(rs.getInt("address_id"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));
            order.setShippingCost(rs.getBigDecimal("shipping_cost"));
            order.setStatus(rs.getString("status"));
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            return order;
        }, id);

        return results.stream().findFirst();
    }

    public List<Order> findAll() {
        String sql = "SELECT id, user_id, address_id, total_amount, shipping_cost, status, order_date FROM orders ORDER BY order_date DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setAddressId(rs.getInt("address_id"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));
            order.setShippingCost(rs.getBigDecimal("shipping_cost"));
            order.setStatus(rs.getString("status"));
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            return order;
        });
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            String sql = "INSERT INTO orders (user_id, address_id, total_amount, shipping_cost, status, order_date) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            jdbcTemplate.update(sql,
                order.getUserId(),
                order.getAddressId(),
                order.getTotalAmount(),
                order.getShippingCost(),
                order.getStatus()
            );
        } else {
            String sql = "UPDATE orders SET user_id = ?, address_id = ?, total_amount = ?, shipping_cost = ?, status = ? WHERE id = ?";

            jdbcTemplate.update(sql,
                order.getUserId(),
                order.getAddressId(),
                order.getTotalAmount(),
                order.getShippingCost(),
                order.getStatus(),
                order.getId()
            );
        }

        return order;
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
