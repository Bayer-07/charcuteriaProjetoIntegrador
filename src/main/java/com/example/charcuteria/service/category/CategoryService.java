package com.example.charcuteria.service.category;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.category.CategoryEditRequestDto;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.dto.category.CategoryResponseDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ProductErrorCode;
import com.example.charcuteria.model.Category;
import com.example.charcuteria.repository.category.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public CategoryEditResponseDto getById(Integer id) {
        return repository.getById(id);
    }

    public CategoryResponseDto returnByName(String name) {
        Category category = repository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return toDTO(category);
    }

    public void createCategory(CategoryRequestDto category) {
        if (repository.createCategory(category) == 0 ) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public void updateCategoryById(CategoryEditRequestDto category) {
        if (repository.updateCategoryById(category) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public void deleteById(Integer id) {
        try {
            int rows = repository.delete(id);
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

    // Mappers de Dto e Entity
    private CategoryResponseDto toDTO(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
