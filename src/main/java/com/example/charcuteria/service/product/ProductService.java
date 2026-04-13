package com.example.charcuteria.service.product;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.user.AdminProductsEditResponseDto;
import com.example.charcuteria.dto.user.AdminProductsRequestDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ProductErrorCode;
import com.example.charcuteria.repository.product.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int getCategoryIdByName(String category) {
        return productRepository.getCategoryIdByName(category);
    }

    public void createProduct(AdminProductsRequestDto product, int categoryId, String image) {
        if (productRepository.createProduct(product, categoryId, image) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public AdminProductsEditResponseDto getById(Integer id) {
        return productRepository.getById(id);
    }

    public String deleteById(Integer id) {
        if (productRepository.deleteById(id) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
        return productRepository.getFileNameById(id);
    }
}
