package com.example.charcuteria.repository.category;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.model.Category;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Category> findAll() {
        String sql = "SELECT id, name, description FROM categories";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                Category c = new Category();
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                return c;
            }
        );
    }

    public Optional<Category> findById(Integer id) {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";

        List<Category> results = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                Category c = new Category();
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                return c;
            },
            id
        );

        return results.stream().findFirst();
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

            jdbcTemplate.update(
                sql,
                category.getName(),
                category.getDescription()
            );

        } else {
            String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";

            jdbcTemplate.update(
                sql,
                category.getName(),
                category.getDescription(),
                category.getId()
            );
        }

        return category;
    }

    public void delete(Category category) {
        String sql = "DELETE FROM categories WHERE id = ?";

        jdbcTemplate.update(sql, category.getId());
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);

        return count != null && count > 0;
    }

    public Optional<Category> findByName(String name) {
        String sql = "SELECT id, name, description FROM categories WHERE name = ?";

        List<Category> results = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                Category c = new Category();
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                return c;
            },
            name
        );

        return results.stream().findFirst();
    }
}
