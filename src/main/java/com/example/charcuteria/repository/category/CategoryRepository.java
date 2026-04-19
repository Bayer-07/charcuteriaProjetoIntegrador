package com.example.charcuteria.repository.category;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.model.Category;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Category findById(Integer id) {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";

        return jdbcTemplate.queryForObject(sql,
            (rs, rowNum) -> new Category(
                rs.getString("name"),
                rs.getString("description")
            ),
            id
        );
    }

    public boolean hasProductWithCategoryId(Integer id) {
        String sql = "SELECT p.id FROM products p WHERE p.category_id = ? AND is_active = TRUE LIMIT 1";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    public int createCategory(CategoryRequestDto category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        return jdbcTemplate.update(sql, category.getName(), category.getDescription());
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        return jdbcTemplate.update(sql, id);
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
