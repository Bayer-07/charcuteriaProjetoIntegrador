package com.example.charcuteria.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.charcuteria.dto.CategoryRequest;
import com.example.charcuteria.dto.CategoryResponse;
import com.example.charcuteria.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/categories/all")
    public List<CategoryResponse> getAll() {
        return service.returnAll();
    }

    @GetMapping("/categories/{id}")
    public CategoryResponse getById(@PathVariable Integer id) {
        return service.returnById(id);
    }

    @GetMapping("/categories/{name}")
    public CategoryResponse getById(@PathVariable String name) {
        return service.returnByName(name);
    }

    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest request) {
        return service.create(request);
    }

    @PutMapping("/categories/update/{id}")
    public CategoryResponse update(@PathVariable Integer id, @RequestBody CategoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}