package com.example.charcuteria.unit.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.example.charcuteria.dto.product.ProductCatalogResponseDto;
import com.example.charcuteria.dto.product.ProductsEditRequestDto;
import com.example.charcuteria.dto.product.ProductsEditResponseDto;
import com.example.charcuteria.dto.product.ProductsRequestDto;
import com.example.charcuteria.dto.product.TopProductResponseDto;
import com.example.charcuteria.repository.product.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductRepository productRepository;

    @Test
    void testGetById_ReturnsProductsEditResponseDto() {
        Integer id = 1;
        ProductsEditResponseDto mockDto = new ProductsEditResponseDto("Salame", "Bom", "Frios", BigDecimal.TEN, 10, "foto.jpg");

        when(jdbcTemplate.queryForObject(any(String.class), any(RowMapper.class), eq(id))).thenReturn(mockDto);

        ProductsEditResponseDto result = productRepository.getById(id);

        assertNotNull(result);
        assertEquals("Salame", result.getName());
        verify(jdbcTemplate).queryForObject(any(String.class), any(RowMapper.class), eq(id));
    }

    @Test
    void testCreateProduct_ReturnsRowsAffected() {
        ProductsRequestDto dto = new ProductsRequestDto();
        dto.setName("Copa");
        dto.setDescription("Defumada");
        dto.setPrice(BigDecimal.ONE);
        dto.setStock(5);
        int categoryId = 2;
        String image = "copa.jpg";

        when(jdbcTemplate.update(any(String.class), eq(categoryId), eq("Copa"), eq("Defumada"), eq(BigDecimal.ONE), eq(5), eq(image))).thenReturn(1);

        int result = productRepository.createProduct(dto, categoryId, image);

        assertEquals(1, result);
    }

    @Test
    void testUpdateProductById_ReturnsRowsAffected() {
        ProductsEditRequestDto dto = new ProductsEditRequestDto();
        dto.setId(1);
        dto.setName("Salsicha");
        dto.setDescription("Viena");
        dto.setPrice(BigDecimal.TEN);
        dto.setStock(20);
        int categoryId = 3;
        String fileName = "viena.jpg";

        when(jdbcTemplate.update(any(String.class), eq(categoryId), eq("Salsicha"), eq("Viena"), eq(BigDecimal.TEN), eq(20), eq(fileName), eq(1))).thenReturn(1);

        int result = productRepository.updateProductById(dto, categoryId, fileName);

        assertEquals(1, result);
    }

    @Test
    void testDeleteProductById_ReturnsRowsAffected() {
        Integer id = 1;
        when(jdbcTemplate.update(any(String.class), eq(id))).thenReturn(1);

        int result = productRepository.deleteProductById(id);

        assertEquals(1, result);
    }

    @Test
    void testGetCategoryIdByName_ReturnsId() {
        String category = "Frios";
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(category))).thenReturn(5);

        int result = productRepository.getCategoryIdByName(category);

        assertEquals(5, result);
    }

    @Test
    void testGetFileNameById_ReturnsString() {
        Integer id = 1;
        when(jdbcTemplate.queryForObject(any(String.class), eq(String.class), eq(id))).thenReturn("imagem.png");

        String result = productRepository.getFileNameById(id);

        assertEquals("imagem.png", result);
    }

    @Test
    void testGetCategoryNameById_ReturnsId() {
        String name = "Premium";
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(name))).thenReturn(10);

        Integer result = productRepository.getCategoryNameById(name);

        assertEquals(10, result);
    }

    @Test
    void testGetTopPurchasedProducts_ReturnsList() {
        int limit = 2;
        TopProductResponseDto item1 = org.mockito.Mockito.mock(TopProductResponseDto.class);
        TopProductResponseDto item2 = org.mockito.Mockito.mock(TopProductResponseDto.class);
        List<TopProductResponseDto> mockList = List.of(item1, item2);

        when(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq(limit))).thenReturn(mockList);

        List<TopProductResponseDto> result = productRepository.getTopPurchasedProducts(limit);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllProductsForCatalog_ReturnsList() {
        ProductCatalogResponseDto item = org.mockito.Mockito.mock(ProductCatalogResponseDto.class);
        List<ProductCatalogResponseDto> mockList = List.of(item);

        when(jdbcTemplate.query(any(String.class), any(RowMapper.class))).thenReturn(mockList);

        List<ProductCatalogResponseDto> result = productRepository.getAllProductsForCatalog();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}