package com.example.charcuteria.controller.purchase;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.dto.purchase.CheckoutRequest;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.purchase.CheckoutService;
import com.example.charcuteria.service.purchase.PixService.PixResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final CheckoutService checkoutService;

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute CheckoutRequest request,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal User user,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selecione um endereço de entrega válido.");
            return "redirect:/cart";
        }

        try {
            PixResult pixResult = checkoutService.processCheckout(request, user.getId());

            model.addAttribute("qrCodeBase64", pixResult.qrCodeBase64());
            model.addAttribute("pixPayload", pixResult.payload());
            model.addAttribute("amount", pixResult.amount());
            model.addAttribute("txid", pixResult.txid());

            return "payment-qr";

        } catch (Exception e) {
            log.error("Checkout failed for user ID {}", user.getId(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }
}
