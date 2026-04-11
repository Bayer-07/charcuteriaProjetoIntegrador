package com.example.charcuteria.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.user.AdminProductsResponseDto;
import com.example.charcuteria.model.Category;
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

    public List<AdminProductsResponseDto> listProducts() {
        return adminRepository.findAllProducts();
    }

    public List<Category> getAllCategories() {
        return adminRepository.getAllCategories();
    }
}
