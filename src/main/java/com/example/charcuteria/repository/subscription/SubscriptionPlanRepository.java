package com.example.charcuteria.repository.subscription;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.model.SubscriptionPlan;

@Repository
public class SubscriptionPlanRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionPlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SubscriptionPlan> findAll() {
        String sql = "SELECT id, name, description, price, is_active FROM subscription_plans WHERE is_active = TRUE";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                SubscriptionPlan p = new SubscriptionPlan();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setIsActive(rs.getBoolean("is_active"));
                return p;
            }
        );
    }

    public Optional<SubscriptionPlan> findById(Integer id) {
        String sql = "SELECT id, name, description, price, is_active FROM subscription_plans WHERE id = ?";

        List<SubscriptionPlan> results = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                SubscriptionPlan p = new SubscriptionPlan();
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setIsActive(rs.getBoolean("is_active"));
                return p;
            },
            id
        );

        return results.stream().findFirst();
    }

    public SubscriptionPlan save(SubscriptionPlan plan) {
        if (plan.getId() == null) {
            String sql = "INSERT INTO subscription_plans (name, description, price, is_active) VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(
                sql,
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getIsActive()
            );
        } else {
            String sql = "UPDATE subscription_plans SET name = ?, description = ?, price = ?, is_active = ? WHERE id = ?";

            jdbcTemplate.update(
                sql,
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getIsActive(),
                plan.getId()
            );
        }

        return plan;
    }

    public void delete(SubscriptionPlan plan) {
        String sql = "UPDATE subscription_plans SET is_active = FALSE WHERE id = ?";

        jdbcTemplate.update(sql, plan.getId());
    }
}
