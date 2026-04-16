package com.example.charcuteria.service.product;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.product.ProductsEditRequestDto;
import com.example.charcuteria.dto.product.ProductsEditResponseDto;
import com.example.charcuteria.dto.product.ProductsRequestDto;
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

    public void createProduct(ProductsRequestDto product, int categoryId, String image) {
        if (productRepository.createProduct(product, categoryId, image) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public ProductsEditResponseDto getById(Integer id) {
        return productRepository.getById(id);
    }

    public void deleteProductById(Integer id) {
        if (productRepository.deleteProductById(id) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public String findFileNameById(Integer id) {
        return productRepository.getFileNameById(id);
    }

    public void updateProductById(ProductsEditRequestDto product, String imageName) {
        int categoryId = productRepository.getCategoryIdByName(product.getCategory());
        if (productRepository.updateProductById(product, categoryId, imageName) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

}
