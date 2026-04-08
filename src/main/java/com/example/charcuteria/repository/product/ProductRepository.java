package com.example.charcuteria.repository.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int deleteById(Integer id) {
        String sql = "UPDATE products SET is_active = FALSE WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }
}
