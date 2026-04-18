package com.example.charcuteria.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.category.CategoryResponse;
import com.example.charcuteria.dto.product.ProductsResponseDto;
import com.example.charcuteria.repository.user.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public int getOrderCount() {
        return adminRepository.getOrderCount();
    }

    public double getActualPrice() {
        return adminRepository.getActualPrice();
    }

    public int getActiveSubscription() {
        return adminRepository.getActiveSubscription();
    }

    public int getProductStorage() {
        return adminRepository.getProductStorage();
    }

    public List<ProductsResponseDto> listProducts() {
        return adminRepository.findAllProducts();
    }

    public List<CategoryResponse> getAllCategories() {
        return adminRepository.getAllCategories();
    }
}
