package com.example.charcuteria.repository.subscription;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.model.Subscription;

@Repository
public class SubscriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Subscription> findAll() {
        String sql = "SELECT id, user_id, plan_id, status, started_at FROM subscriptions";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                Subscription s = new Subscription();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setPlanId(rs.getInt("plan_id"));
                s.setStatus(rs.getString("status"));
                s.setStartedAt(rs.getString("started_at"));
                return s;
            }
        );
    }

    public Optional<Subscription> findById(Integer id) {
        String sql = "SELECT id, user_id, plan_id, status, started_at FROM subscriptions WHERE id = ?";

        List<Subscription> results = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                Subscription s = new Subscription();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setPlanId(rs.getInt("plan_id"));
                s.setStatus(rs.getString("status"));
                s.setStartedAt(rs.getString("started_at"));
                return s;
            },
            id
        );

        return results.stream().findFirst();
    }

    public List<Subscription> findByUserId(Integer userId) {
        String sql = "SELECT id, user_id, plan_id, status, started_at FROM subscriptions WHERE user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Subscription s = new Subscription();
            s.setId(rs.getInt("id"));
            s.setUserId(rs.getInt("user_id"));
            s.setPlanId(rs.getInt("plan_id"));
            s.setStatus(rs.getString("status"));
            s.setStartedAt(rs.getString("started_at"));
            return s;
        }, userId);
    }

    public Subscription save(Subscription subscription) {
        if (subscription.getId() == null) {
            String sql = "INSERT INTO subscriptions (user_id, plan_id, status, started_at) VALUES (?, ?, ?, CURRENT_DATE)";

            jdbcTemplate.update(
                sql,
                subscription.getUserId(),
                subscription.getPlanId(),
                subscription.getStatus()
            );
        } else {
            String sql = "UPDATE subscriptions SET user_id = ?, plan_id = ?, status = ? WHERE id = ?";

            jdbcTemplate.update(
                sql,
                subscription.getUserId(),
                subscription.getPlanId(),
                subscription.getStatus(),
                subscription.getId()
            );
        }

        return subscription;
    }

    public void delete(Subscription subscription) {
        String sql = "DELETE FROM subscriptions WHERE id = ?";

        jdbcTemplate.update(sql, subscription.getId());
    }
}
