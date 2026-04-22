package com.example.charcuteria.controller.category;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.charcuteria.dto.category.CategoryEditRequestDto;
import com.example.charcuteria.dto.category.CategoryEditResponseDto;
import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.service.category.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CategoryEditResponseDto getById(@PathVariable Integer id) {
        var response = categoryService.getById(id);
        if (response != null) return response;
        throw new RuntimeException();
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("categoryDto") CategoryEditRequestDto category, Model model) {
        categoryService.updateCategoryById(category);
        return "redirect:/admin/products?type=categories";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("categoryDto") CategoryRequestDto category, Model model) {
        try {
            categoryService.createCategory(category);
            return "redirect:/admin/products?type=categories";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/products?type=products";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteById(@PathVariable Integer id) {
        try {
            categoryService.deleteById(id);
            return "redirect:/admin/products?type=categories";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/products?type=products";
        }
    }
}
