package com.example.charcuteria.service.product;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.user.AdminProductsEditRequestDto;
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

    public void deleteById(Integer id) {
        if (productRepository.deleteById(id) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public String findFileNameById(Integer id) {
        return productRepository.getFileNameById(id);
    }

    public void updateProductById(AdminProductsEditRequestDto product, String imageName) {
        int categoryId = productRepository.getCategoryIdByName(product.getCategory());
        if (productRepository.updateProductById(product, categoryId, imageName) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

}
