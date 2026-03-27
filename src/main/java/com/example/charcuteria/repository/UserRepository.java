package com.example.charcuteria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.enums.UserRoleEnum;
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

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash, role FROM users WHERE email = ?";

        List<User> results = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                Enum.valueOf(UserRoleEnum.class, rs.getString("role"))
            ),
            email
        );
        // acha o primeiro, nesse caso acha o certo, nao tem como ter 2 emails iguais
        return results.stream().findFirst();
    }

    public void createUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        jdbcTemplate.update(
            sql,
            user.getName(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getRole().name()
        );
    }

}
