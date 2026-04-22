package com.example.charcuteria.controller.product;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.charcuteria.dto.product.ProductsResponseDto;
import com.example.charcuteria.service.product.ProductService;

@Controller
@RequestMapping("/produtos")
public class CustomerProductController {
    private final ProductService productService;

    public CustomerProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getAllProducts(Model model) {
        List<ProductsResponseDto> products = productService.getProducts();
        model.addAttribute("products", products);
        return "public/produtos";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Object getProduct(@PathVariable Integer id) {
        var response = productService.getById(id);
        if (response != null)
            return response;
        throw new RuntimeException();
    }
}

