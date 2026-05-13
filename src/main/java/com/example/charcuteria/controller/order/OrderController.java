package com.example.charcuteria.controller.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.charcuteria.model.User;
import com.example.charcuteria.service.order.OrderService;

@Controller
@RequestMapping("/user/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(@AuthenticationPrincipal User loggedUser, Model model) {
        if (loggedUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.findByUserId(loggedUser.getId()));
        return "public/orders-list";
    }

    @GetMapping("/{id}")
    public String getOrderDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "user/order-detail";
    }
}
