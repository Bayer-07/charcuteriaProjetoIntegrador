package com.example.charcuteria.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.charcuteria.dto.subscription.SubscriptionPlanResponse;
import com.example.charcuteria.dto.subscription.SubscriptionRequest;
import com.example.charcuteria.dto.subscription.UserSubscriptionResponseDto;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.subscription.SubscriptionPlanService;
import com.example.charcuteria.service.subscription.SubscriptionService;

@Controller
@RequestMapping("/user")
public class UserSubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanService subscriptionPlanService;

    public UserSubscriptionController(SubscriptionService subscriptionService, SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionService = subscriptionService;
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping("/subscriptions")
    public String showSubscriptions(@AuthenticationPrincipal User loggedUser, Model model) {
        try {
            Optional<UserSubscriptionResponseDto> subscription = subscriptionService.getActiveSubscriptionByUserId(loggedUser.getId());
            
            if (subscription.isPresent()) {
                model.addAttribute("subscription", subscription.get());
                model.addAttribute("hasSubscription", true);
            } else {
                model.addAttribute("hasSubscription", false);
            }
            
            return "public/subscriptions";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar assinaturas");
            return "public/subscriptions";
        }
    }

    @GetMapping("/subscribe")
    public String showPlans(@AuthenticationPrincipal User loggedUser, Model model) {
        try {
            List<SubscriptionPlanResponse> plans = subscriptionPlanService.returnAll();
            model.addAttribute("plans", plans);
            return "public/subscribe";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar planos");
            return "public/subscribe";
        }
    }

    @PostMapping("/subscribe")
    public String subscribeToPlan(@AuthenticationPrincipal User loggedUser, @RequestParam Integer planId, Model model) {
        try {
            // Check if user already has an active subscription
            Optional<UserSubscriptionResponseDto> existingSubscription = subscriptionService.getActiveSubscriptionByUserId(loggedUser.getId());
            
            if (existingSubscription.isPresent()) {
                model.addAttribute("error", "Você já possui uma assinatura ativa");
                List<SubscriptionPlanResponse> plans = subscriptionPlanService.returnAll();
                model.addAttribute("plans", plans);
                return "public/subscribe";
            }

            // Create subscription
            SubscriptionRequest request = new SubscriptionRequest();
            request.setUserId(loggedUser.getId());
            request.setPlanId(planId);
            request.setStatus("ACTIVE");

            subscriptionService.create(request);
            return "redirect:/user/subscriptions";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao criar assinatura: " + e.getMessage());
            List<SubscriptionPlanResponse> plans = subscriptionPlanService.returnAll();
            model.addAttribute("plans", plans);
            return "public/subscribe";
        }
    }
}
