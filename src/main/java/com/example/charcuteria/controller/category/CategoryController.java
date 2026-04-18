package com.example.charcuteria.controller.category;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.category.CategoryRequest;
import com.example.charcuteria.dto.category.CategoryResponse;
import com.example.charcuteria.service.category.CategoryService;

@Controller
@RequestMapping("/categories")
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

    @PostMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryRequest());
        return "category/form";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CategoryResponse response = service.returnById(id);
        CategoryRequest request = new CategoryRequest();

        request.setName(response.getName());
        request.setDescription(response.getDesc());

        model.addAttribute("category", request);
        model.addAttribute("id", id);

        return "category/form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("category") CategoryRequest request) {
        service.update(id, request);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/categories";
    }
}
