package com.example.charcuteria.unit.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.charcuteria.dto.category.CategoryEditRequestDto;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ProductErrorCode;
import com.example.charcuteria.repository.category.CategoryRepository;
import com.example.charcuteria.service.category.CategoryService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testGetById() {
        CategoryEditResponseDto mockResponse = new CategoryEditResponseDto("Defumados", "Descrição");
        when(categoryRepository.getById(1)).thenReturn(mockResponse);

        CategoryEditResponseDto result = categoryService.getById(1);

        assertNotNull(result);
        assertEquals("Defumados", result.getName());
        verify(categoryRepository).getById(1);
    }

    @Test
    void testCreateCategory_Success() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Embutidos", "Descrição");
        when(categoryRepository.createCategory(requestDto)).thenReturn(1);

        categoryService.createCategory(requestDto);

        verify(categoryRepository).createCategory(requestDto);
    }

    @Test
    void testCreateCategory_ThrowsBusinessException() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Embutidos", "Descrição");
        when(categoryRepository.createCategory(requestDto)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.createCategory(requestDto);
        });

        assertEquals(ProductErrorCode.PRODUCT_NOT_FOUND, exception.getProductErrorCode());
    }

    @Test
    void testUpdateCategoryById_Success() {
        CategoryEditRequestDto requestDto = new CategoryEditRequestDto(1, "Copa", "Descrição Copa");
        when(categoryRepository.updateCategoryById(requestDto)).thenReturn(1);

        categoryService.updateCategoryById(requestDto);

        verify(categoryRepository).updateCategoryById(requestDto);
    }

    @Test
    void testUpdateCategoryById_ThrowsBusinessException() {
        CategoryEditRequestDto requestDto = new CategoryEditRequestDto(1, "Copa", "Descrição Copa");
        when(categoryRepository.updateCategoryById(requestDto)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            categoryService.updateCategoryById(requestDto);
        });

        assertEquals(ProductErrorCode.PRODUCT_NOT_FOUND, exception.getProductErrorCode());
    }

    @Test
    void testDeleteById_Success() {
        when(categoryRepository.deleteById(1)).thenReturn(1);

        categoryService.deleteById(1);

        verify(categoryRepository).deleteById(1);
    }

    @Test
    void testDeleteById_NotFound_ThrowsEntityNotFoundException() {
        when(categoryRepository.deleteById(1)).thenReturn(0);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteById(1);
        });

        assertEquals("Category with ID 1 not found", exception.getMessage());
    }

    @Test
    void testDeleteById_ActiveProductsLinked_ThrowsRuntimeException() {
        // Simulamos o erro de chave estrangeira/produtos vinculados do banco de dados
        Throwable rootCause = new Throwable("Error: active products exist for this category");
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database Error", rootCause);
        
        when(categoryRepository.deleteById(1)).thenThrow(dbException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteById(1);
        });

        assertEquals("Cannot delete: active products linked.", exception.getMessage());
    }

    @Test
    void testDeleteById_GenericDatabaseError_ReThrowsException() {
        // Simulamos um erro genérico do banco que não tem a ver com produtos ativos vinculados
        Throwable rootCause = new Throwable("Connection timeout");
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database Error", rootCause);

        when(categoryRepository.deleteById(1)).thenThrow(dbException);

        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryService.deleteById(1);
        });
    }
}