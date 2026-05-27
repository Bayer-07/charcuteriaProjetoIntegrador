package com.example.charcuteria.service.subscription;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/subscriptions")

public class SubscriptionAdminView {
    private final SubscriptionService service;

    public SubscriptionAdminView(SubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("subscriptions", service.returnAll());
        return "admin/subscriptions";
    }
}
