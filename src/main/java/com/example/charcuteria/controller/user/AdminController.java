package com.example.charcuteria.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.charcuteria.dto.product.ProductsRequestDto;
import com.example.charcuteria.dto.user.AdminDashboardResponseDto;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.category.CategoryService;
import com.example.charcuteria.service.user.AdminService;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;

    public AdminController(AdminService adminService, CategoryService categoryService) {
        this.adminService = adminService;
        this.categoryService = categoryService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal User loggedUser, Model model) {
        // IDEA: a gente pode/vai fazer algum filtro por mes aq?
        // parece legal e nao é tao dificil
        // se alguem quiser fazer -> é so receber um mes vindo do front, ir passando pro service e tal e filtrar nas queries
        try {
            int qntPedidos = adminService.getOrderCount();
            double valorDoMes = adminService.getActualPrice();
            int subsAtivo = adminService.getActiveSubscription();
            int productsWithLowStorage = adminService.getProductStorage();

            AdminDashboardResponseDto response = new AdminDashboardResponseDto(qntPedidos, valorDoMes, subsAtivo, productsWithLowStorage);
            model.addAttribute("data", response);
            return "admin/dashboardAdmin";
        } catch (Exception e) {
            model.addAttribute("registrationError", "Internal server error, try again later please");
            return "admin/dashboardAdmin";
        }
    }

    @GetMapping("/products")
    public String showProductsDashboard(@RequestParam(value = "type", required = false, defaultValue = "products") String type,@AuthenticationPrincipal User loggedUser, Model model) {
        try {
            model.addAttribute("type", type);
            model.addAttribute("categories", categoryService.returnAll());
            model.addAttribute("productDto", new ProductsRequestDto());

            if ("products".equals(type)) {
                model.addAttribute("products", adminService.listProducts());
            }

            return "admin/productsDashboard";
        } catch (Exception e) {
            return "admin/productsDashboard";
        }
    }
}
