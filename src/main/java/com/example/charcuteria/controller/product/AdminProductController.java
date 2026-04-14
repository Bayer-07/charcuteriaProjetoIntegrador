package com.example.charcuteria.controller.product;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.charcuteria.dto.user.AdminProductsEditRequestDto;
import com.example.charcuteria.dto.user.AdminProductsEditResponseDto;
import com.example.charcuteria.dto.user.AdminProductsRequestDto;
import com.example.charcuteria.service.product.FileStorageService;
import com.example.charcuteria.service.product.ProductService;

import ch.qos.logback.core.model.Model;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public AdminProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") AdminProductsRequestDto product, BindingResult result, Model model) {
        if (result.hasErrors()) return "redirect:/admin/products";

        try {
            String imageName = fileStorageService.saveFile(product.getImage());

            int categoryId = productService.getCategoryIdByName(product.getCategory());
            productService.createProduct(product, categoryId, imageName);

            return "redirect:/admin/products";
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProductById(@PathVariable Integer id, Model model) {
        try {
            String fileName = productService.findFileNameById(id);
            productService.deleteById(id);
            fileStorageService.deleteFile(fileName);
            return "redirect:/admin/products";
        } catch (IOException e) {
            System.out.println(e);
            return "redirect:/admin/products";
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public AdminProductsEditResponseDto getProductById(@PathVariable Integer id) {
        var response = productService.getById(id);
        if (response != null) return response;
        throw new RuntimeException();
    }

    @PostMapping("update")
    public String updateProductById(@Valid @ModelAttribute("productDto") AdminProductsEditRequestDto product, Model model) {
        try {
            // System.out.println(product.getId());
            // System.out.println(product.getName());
            // System.out.println(product.getDescription());
            // System.out.println(product.getCategory());
            // System.out.println(product.getPrice());
            // System.out.println(product.getStock());
            // System.out.println(product.getFile());

            String fileName = productService.findFileNameById(product.getId());
            String newImageName = fileStorageService.saveFile(product.getFile());

            // essa SQL ta errada, tenho que pegar o id da categoria primeiro e depois passar pra trocar no db
            productService.updateProductById(product, newImageName);

            fileStorageService.deleteFile(fileName);
            return "redirect:/admin/products";
        } catch(Exception e) {
            System.out.println(e);
            return "redirect:/admin/products";
        }
    }
}
