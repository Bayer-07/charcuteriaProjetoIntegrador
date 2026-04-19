package com.example.charcuteria.controller.category;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.dto.category.CategoryResponseDto;
import com.example.charcuteria.service.category.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", service.returnAll());
        return "category/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model) {
        model.addAttribute("category", service.returnById(id));
        return "category/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryRequestDto());
        return "category/form";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CategoryResponseDto response = service.returnById(id);
        CategoryRequestDto request = new CategoryRequestDto(
                                        response.getName(),
                                        response.getDescription()
                                    );

        model.addAttribute("category", request);
        model.addAttribute("id", id);

        return "category/form";
    }

    @PostMapping("/create")
    public String createCategory(Model model) {
        return "admin/products";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("category") CategoryRequestDto request) {
        service.update(id, request);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/categories";
    }
}
