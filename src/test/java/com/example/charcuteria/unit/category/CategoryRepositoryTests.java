package com.example.charcuteria.unit.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.example.charcuteria.dto.category.CategoryEditRequestDto;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.repository.category.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CategoryRepository categoryRepository;

    @Test
    void testGetById_Success() {
        CategoryEditResponseDto mockResponse = new CategoryEditResponseDto("Defumados",
                "Produtos defumados artesanais");

        // Mock do queryForObject aceitando o RowMapper tipado corretamente para evitar
        // Type Safety warnings
        when(jdbcTemplate.queryForObject(
                anyString(),
                ArgumentMatchers.<RowMapper<CategoryEditResponseDto>>any(),
                eq(1))).thenReturn(mockResponse);

        CategoryEditResponseDto result = categoryRepository.getById(1);

        assertNotNull(result);
        assertEquals("Defumados", result.getName());
        assertEquals("Produtos defumados artesanais", result.getDescription());
        verify(jdbcTemplate).queryForObject(anyString(), ArgumentMatchers.<RowMapper<CategoryEditResponseDto>>any(),
                eq(1));
    }

    @Test
    void testGetById_NotFound_ThrowsEmptyResultException() {
        when(jdbcTemplate.queryForObject(
                anyString(),
                ArgumentMatchers.<RowMapper<CategoryEditResponseDto>>any(),
                eq(1))).thenThrow(new EmptyResultDataAccessException(1));

        assertThrows(EmptyResultDataAccessException.class, () -> {
            categoryRepository.getById(1);
        });
    }

    @Test
    void testUpdateCategoryById_Success() {
        CategoryEditRequestDto requestDto = new CategoryEditRequestDto(1, "Embutidos", "Nova descrição");

        when(jdbcTemplate.update(anyString(), eq("Embutidos"), eq("Nova descrição"), eq(1)))
                .thenReturn(1);

        Integer rowsAffected = categoryRepository.updateCategoryById(requestDto);

        assertEquals(1, rowsAffected);
        verify(jdbcTemplate).update(anyString(), eq("Embutidos"), eq("Nova descrição"), eq(1));
    }

    @Test
    void testCreateCategory_Success() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Salames", "Salames curados");

        when(jdbcTemplate.update(anyString(), eq("Salames"), eq("Salames curados")))
                .thenReturn(1);

        int rowsAffected = categoryRepository.createCategory(requestDto);

        assertEquals(1, rowsAffected);
        verify(jdbcTemplate).update(anyString(), eq("Salames"), eq("Salames curados"));
    }

    @Test
    void testDeleteById_Success() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);

        int rowsAffected = categoryRepository.deleteById(1);

        assertEquals(1, rowsAffected);
        verify(jdbcTemplate).update(anyString(), eq(1));
    }
}