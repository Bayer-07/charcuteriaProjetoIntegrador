package com.example.charcuteria.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        List<UserSubscriptionResponseDto> subscriptions = subscriptionService.getAllActiveSubscriptionsByUserId(loggedUser.getId());

        if (!subscriptions.isEmpty()) {
            model.addAttribute("subscriptions", subscriptions);
            model.addAttribute("hasSubscriptions", true);
        } else {
            model.addAttribute("hasSubscriptions", false);
        }

        return "user/subscriptions";
    }

    @GetMapping("/subscribe")
    public String showPlans(@AuthenticationPrincipal User loggedUser, Model model) {
        List<SubscriptionPlanResponse> plans = subscriptionPlanService.returnAll();

        model.addAttribute("plans", plans);
        return "public/subscribe";
    }

    @PostMapping("/subscribe")
    public String subscribeToPlan(@AuthenticationPrincipal User loggedUser, @RequestParam Integer planId, Model model) {
        Optional<UserSubscriptionResponseDto> existingSubscription = subscriptionService.getActiveSubscriptionByUserId(loggedUser.getId());

        if (existingSubscription.isPresent()) {
            model.addAttribute("error", "Você já possui uma assinatura ativa");
            List<SubscriptionPlanResponse> plans = subscriptionPlanService.returnAll();
            model.addAttribute("plans", plans);
            return "public/subscriptions-options";
        }

        SubscriptionRequest request = new SubscriptionRequest();
        request.setUserId(loggedUser.getId());
        request.setPlanId(planId);
        request.setStatus("ACTIVE");

        subscriptionService.create(request);
        return "redirect:/user/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/pause")
    public String pauseSubscription(@AuthenticationPrincipal User loggedUser, @PathVariable Integer id) {
        subscriptionService.updateSubscriptionStatus(id, loggedUser.getId(), "PAUSED");
        return "redirect:/user/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/renew")
    public String renewSubscription(@AuthenticationPrincipal User loggedUser, @PathVariable Integer id) {
        subscriptionService.updateSubscriptionStatus(id, loggedUser.getId(), "ACTIVE");
        return "redirect:/user/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/cancel")
    public String cancelSubscription(@AuthenticationPrincipal User loggedUser, @PathVariable Integer id) {
        subscriptionService.updateSubscriptionStatus(id, loggedUser.getId(), "CANCELLED");
        return "redirect:/user/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/reactivate")
    public String reactivateSubscription(@AuthenticationPrincipal User loggedUser, @PathVariable Integer id) {
        subscriptionService.updateSubscriptionStatus(id, loggedUser.getId(), "ACTIVE");
        return "redirect:/user/subscriptions";
    }
}
