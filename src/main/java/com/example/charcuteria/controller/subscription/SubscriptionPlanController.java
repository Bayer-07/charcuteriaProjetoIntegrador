package com.example.charcuteria.controller.subscription;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.charcuteria.dto.subscription.SubscriptionPlanRequest;
import com.example.charcuteria.dto.subscription.SubscriptionPlanResponse;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.subscription.SubscriptionPlanService;

@Controller
@RequestMapping("/admin/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    public SubscriptionPlanController(SubscriptionPlanService service) {
        this.service = service;
    }

    @GetMapping
    public String listPlans(Model model) {
        model.addAttribute("plans", service.returnAll());
        return "subscription/plans-list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model) {
        model.addAttribute("plan", service.returnById(id));
        return "subscription/plan-detail";
    }

    @PostMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("plan", new SubscriptionPlanRequest());
        return "subscription/plan-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        SubscriptionPlanResponse response = service.returnById(id);
        SubscriptionPlanRequest request = new SubscriptionPlanRequest();

        request.setName(response.getName());
        request.setDescription(response.getDescription());
        request.setPrice(response.getPrice());

        model.addAttribute("plan", request);
        model.addAttribute("id", id);

        return "subscription/plan-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("plan") SubscriptionPlanRequest request) {
        service.update(id, request);
        return "redirect:/admin/subscription-plans";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/admin/subscription-plans";
    }
}
