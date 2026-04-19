package com.example.charcuteria.controller.category;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.service.category.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model) {
        model.addAttribute("category", service.returnById(id));
        return "category/detail";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("categoryDto") CategoryRequestDto category, Model model) {
        try {
            service.createCategory(category);
            return "redirect:/admin/products?type=categories";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/products?type=products";
        }
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("category") CategoryRequestDto request) {
        service.update(id, request);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        try {
            service.deleteById(id);
            return "redirect:/admin/products?type=categories";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/products?type=products";
        }
    }
}
