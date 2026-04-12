package com.example.charcuteria.repository.user;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.dto.user.AdminProductsResponseDto;
import com.example.charcuteria.model.Category;

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
        String sql = "SELECT COUNT(*) FROM products p WHERE p.stock_quantity < 51 AND p.is_active = TRUE";

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<AdminProductsResponseDto> findAllProducts() {
        String sql = "SELECT p.id, p.stock_quantity, c.name AS category, p.name, p.price FROM products p JOIN categories c ON p.category_id = c.id WHERE p.is_active = TRUE ORDER BY p.id ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new AdminProductsResponseDto(
                rs.getInt("id"),
                rs.getInt("stock_quantity"),
                rs.getString("category"),
                rs.getString("name"),
                rs.getBigDecimal("price")
            )
        );
    }


    public List<Category> getAllCategories() {
        String sql = "SELECT name FROM categories";

        return jdbcTemplate.query(sql, (rs, row) -> {
            return new Category(rs.getString("name"));
        });
    }
}
