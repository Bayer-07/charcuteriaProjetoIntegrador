package com.example.charcuteria.repository.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.dto.user.AdminProductsEditResponseDto;
import com.example.charcuteria.dto.user.AdminProductsRequestDto;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getCategoryIdByName(String category) {
        String sql = "Select id FROM categories WHERE name = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, category);
    }

    public int createProduct(AdminProductsRequestDto product, int categoryId, String image) {
        String sql = "INSERT INTO products (category_id, name, description, price, stock_quantity, image_path) VALUES (?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
            categoryId,
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            image
        );
    }

    public AdminProductsEditResponseDto getById(Integer id) {
        String sql = "SELECT p.name, p.description, c.name AS category, p.price, p.stock_quantity AS stock, p.image_path AS file FROM products p JOIN categories c ON c.id = p.category_id WHERE p.id = ?";

        return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new AdminProductsEditResponseDto(
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                rs.getString("file")
            ),
            id
        );
    }

    public int deleteById(Integer id) {
        String sql = "UPDATE products SET is_active = FALSE, image_path = NULL WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

    public String getFileNameById(Integer id) {
        String sql = "SELECT image_path FROM products WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, String.class, id);
    }
}
