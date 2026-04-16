package com.example.charcuteria.repository.product;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.dto.product.ProductsEditRequestDto;
import com.example.charcuteria.dto.product.ProductsEditResponseDto;
import com.example.charcuteria.dto.product.ProductsRequestDto;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ProductsEditResponseDto getById(Integer id) {
        String sql = "SELECT p.name, p.description, c.name AS category, p.price, p.stock_quantity AS stock, p.image_path AS file FROM products p JOIN categories c ON c.id = p.category_id WHERE p.id = ?";

        return jdbcTemplate.queryForObject(
            sql,
            (rs, rowNum) -> new ProductsEditResponseDto(
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

    public int createProduct(ProductsRequestDto product, int categoryId, String image) {
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

    public int updateProductById(ProductsEditRequestDto product, int categoryId, String fileName) {
        String sql = "UPDATE products SET category_id = ?, name = ?,  description = ?,  price = ?, stock_quantity = ?, image_path = ? WHERE id = ?";

        return jdbcTemplate.update(sql,
            categoryId,
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            fileName,
            product.getId()
        );
    }

    public int deleteProductById(Integer id) {
        String sql = "UPDATE products SET is_active = FALSE, image_path = NULL WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }

    // Helpers
    public int getCategoryIdByName(String category) {
        String sql = "Select id FROM categories WHERE name = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, category);
    }

    public String getFileNameById(Integer id) {
        String sql = "SELECT p.image_path FROM products p WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, String.class, id);
    }

    public Integer getCategoryNameById(String categoryName) {
        String sql = "SELECT c.id FROM categories c WHERE c.name = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, categoryName);
    }

}
