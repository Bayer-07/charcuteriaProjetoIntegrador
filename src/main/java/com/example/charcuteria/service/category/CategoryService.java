package com.example.charcuteria.service.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.dto.category.CategoryResponseDto;
import com.example.charcuteria.model.Category;
import com.example.charcuteria.repository.category.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<CategoryResponseDto> returnAll() {
        return repository.findAll();
    }

    public CategoryResponseDto returnById(Integer id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return toDTO(category);
    }

    public CategoryResponseDto returnByName(String name) {
        Category category = repository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return toDTO(category);
    }

    public CategoryResponseDto create(CategoryRequestDto request) {
        Category category = toEntity(request);
        Category saved = repository.save(category);
        return toDTO(saved);
    }

    public CategoryResponseDto update(Integer id, CategoryRequestDto request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = repository.save(category);
        return toDTO(updated);
    }

    public void deleteById(Integer id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        repository.delete(category);
    }

    // Mappers de Dto e Entity
    private Category toEntity(CategoryRequestDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    private CategoryResponseDto toDTO(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
