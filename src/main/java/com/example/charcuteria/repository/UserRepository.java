package com.example.charcuteria.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.model.User;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    public void createUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        jdbcTemplate.update(
            sql,
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole().name()
        );
    }
}
