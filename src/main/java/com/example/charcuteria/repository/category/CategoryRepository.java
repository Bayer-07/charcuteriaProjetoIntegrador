package com.example.charcuteria.repository.category;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.dto.category.CategoryEditRequestDto;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.dto.category.CategoryRequestDto;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CategoryEditResponseDto getById(Integer id) {
        String sql = "SELECT name, description FROM categories WHERE id = ?";

        return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new CategoryEditResponseDto(
                rs.getString("name"),
                rs.getString("description")
            ),
            id
        );
    }

    public Integer updateCategoryById(CategoryEditRequestDto category) {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";

        return jdbcTemplate.update(
            sql,
            category.getName(),
            category.getDescription(),
            category.getId()
        );
    }

    public int createCategory(CategoryRequestDto category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        return jdbcTemplate.update(sql, category.getName(), category.getDescription());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

}
