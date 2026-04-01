package com.example.charcuteria.repository.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRepository {
    private final JdbcTemplate jdbcTemplate;

    public AdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getOrderCount() {
        // quando criar o enum das orders, se tiver algum "cancelado" tem q mudar o filtro pra nao pegar eles aqui
        // so colocar um WHERE status != "cancelado"
        String sql = "SELECT COUNT(*) FROM orders";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public double getActualPrice() {
        String sql = "SELECT SUM(total_amount) FROM orders";

        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public int getActiveSubscription() {
        String sql = "SELECT COUNT(*) FROM subscriptions WHERE status = 'ACTIVE'";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getProductStorage() {
        String sql = "SELECT COUNT(*) FROM products p WHERE p.stock_quantity < 51 AND p.is_active = 't'";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
