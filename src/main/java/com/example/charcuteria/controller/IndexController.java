package com.example.charcuteria.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.charcuteria.dto.product.TopProductResponseDto;
import com.example.charcuteria.service.product.ProductService;

@Controller
@RequestMapping
public class IndexController {
    private final ProductService productService;

    public IndexController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String Index() {
        return "public/index";
    }

    @GetMapping("/politica")
    public String politica() {
        return "public/politica-de-privacidade";
    }

    @GetMapping("/index")
    public String getIndex() {
        return "redirect:public/index";
    }

    @GetMapping("/index/top-products")
    @ResponseBody
    public List<TopProductResponseDto> getTopPurchasedProducts() {
        return productService.getTopPurchasedProducts(4);
    }
}
