package com.example.charcuteria.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.dto.user.AdminProductsRequestDto;
import com.example.charcuteria.service.product.ProductService;

import ch.qos.logback.core.model.Model;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/delete/{id}")
    public String deleteProductById(@PathVariable Integer id, Model model) {
        try {
            productService.deleteById(id);
            return "redirect:/admin/products";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") AdminProductsRequestDto product, BindingResult result, Model model) {
        if (result.hasErrors()) return "/admin/products";

        try {
            int categoryId = productService.getCategoryIdByName(product.getCategory());
            productService.createProduct(product, categoryId);
            return "redirect:/admin/products";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/dashboard";
        }
    }
}
