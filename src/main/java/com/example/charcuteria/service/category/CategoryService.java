package com.example.charcuteria.service.category;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.category.CategoryEditRequestDto;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ProductErrorCode;
import com.example.charcuteria.repository.category.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryEditResponseDto getById(Integer id) {
        return categoryRepository.getById(id);
    }

    public void createCategory(CategoryRequestDto category) {
        if (categoryRepository.createCategory(category) == 0 ) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public void updateCategoryById(CategoryEditRequestDto category) {
        if (categoryRepository.updateCategoryById(category) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public void deleteById(Integer id) {
        try {
            int rows = categoryRepository.deleteById(id);
            if (rows == 0) {
                throw new EntityNotFoundException("Category with ID " + id + " not found");
            }
        } catch (DataAccessException e) {
            String dbMessage = e.getMostSpecificCause().getMessage();
            if (dbMessage != null && dbMessage.contains("active products exist")) {
                throw new RuntimeException("Cannot delete: active products linked.");
            }
            throw e;
        }
    }

}
