package com.example.charcuteria.controller.subscription;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.dto.subscription.SubscriptionResponse;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.subscription.SubscriptionPlanService;
import com.example.charcuteria.service.subscription.SubscriptionService;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;
    private final SubscriptionPlanService planService;

    public SubscriptionController(SubscriptionService service, SubscriptionPlanService planService) {
        this.service = service;
        this.planService = planService;
    }

    @GetMapping()
    public String listSubscriptions(@AuthenticationPrincipal User loggedUser, Model model) {
        if (loggedUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("plans", planService.returnAll());
        return "public/subscriptions-options";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model) {
        model.addAttribute("subscription", service.returnById(id));
        return "subscription/detail";
    }

    @PostMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("subscription", new SubscriptionRequest());
        model.addAttribute("plans", planService.returnAll());
        return "subscription/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        SubscriptionResponse response = service.returnById(id);
        SubscriptionRequest request = new SubscriptionRequest();

        request.setUserId(response.getUserId());
        request.setPlanId(response.getPlanId());
        request.setStatus(response.getStatus());

        model.addAttribute("subscription", request);
        model.addAttribute("plans", planService.returnAll());
        model.addAttribute("id", id);

        return "subscription/form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("subscription") SubscriptionRequest request) {
        service.update(id, request);
        return "redirect:/subscriptions";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/subscriptions";
    }
}
