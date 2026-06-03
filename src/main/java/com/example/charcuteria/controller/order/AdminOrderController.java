package com.example.charcuteria.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.service.order.OrderService;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String returnAll(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "/admin/orders";
    }
}
