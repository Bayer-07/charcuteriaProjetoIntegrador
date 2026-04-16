package com.example.charcuteria.service.category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.charcuteria.model.Category;
import com.example.charcuteria.repository.category.CategoryRepository;
import com.example.charcuteria.dto.category.CategoryRequest;
import com.example.charcuteria.dto.category.CategoryResponse;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<CategoryResponse> returnAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryResponse returnById(Integer id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return toDTO(category);
    }

    public CategoryResponse returnByName(String name) {
        Category category = repository.findByName(name)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return toDTO(category);
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = toEntity(request);
        Category saved = repository.save(category);
        return toDTO(saved);
    }

    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        category.setName(request.getName());
        category.setDesc(request.getDesc());

        Category updated = repository.save(category);
        return toDTO(updated);
    }

    public void deleteById(Integer id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        repository.delete(category);
    }

    // Mappers de Dto e Entity
    private Category toEntity(CategoryRequest dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDesc(dto.getDesc());
        return category;
    }

    private CategoryResponse toDTO(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDesc(category.getDesc());
        return dto;
    }
}