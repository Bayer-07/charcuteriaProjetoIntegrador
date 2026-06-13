package com.example.charcuteria.controller.product;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.dto.category.CategoryRequestDto;
import com.example.charcuteria.dto.product.ProductsEditRequestDto;
import com.example.charcuteria.dto.product.ProductsEditResponseDto;
import com.example.charcuteria.dto.product.ProductsRequestDto;
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isBlank()) {
                    setValue(null);
                    return;
                }
                setValue(new BigDecimal(text.replace(",", ".")));
            }
        });
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("productDto") ProductsRequestDto product,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            String errorMessage  = result.getAllErrors()
            .get(0)
            .getDefaultMessage();

            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/admin/products";
        }

        try {
            String imageName = fileStorageService.saveFile(product.getImage());

            int categoryId = productService.getCategoryIdByName(product.getCategory());
            productService.createProduct(product, categoryId, imageName);

            return "redirect:/admin/products";

        } catch (Exception e) {
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ProductsEditResponseDto getProductById(@PathVariable Integer id) {
        var response = productService.getById(id);
        if (response != null)
            return response;
        throw new RuntimeException();
    }

    @PostMapping("/update")
    public String updateProductById(@Valid @ModelAttribute("productDto") ProductsEditRequestDto product, Model model) {
        try {
            String fileName = productService.findFileNameById(product.getId());
            String newImageName = fileName;

            if (product.getFile() != null && !product.getFile().isEmpty()) {
                newImageName = fileStorageService.saveFile(product.getFile());
                fileStorageService.deleteFile(fileName);
            }

            productService.updateProductById(product, newImageName);

            return "redirect:/admin/products";
        } catch (IOException e) {
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProductById(@PathVariable Integer id, Model model) {
        try {
            String fileName = productService.findFileNameById(id);
            productService.deleteProductById(id);
            fileStorageService.deleteFile(fileName);
            return "redirect:/admin/products";
        } catch (IOException e) {
            return "redirect:/admin/products";
        }
    }

}
