package com.example.charcuteria.controller.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.model.User;
import com.example.charcuteria.service.cart.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public String addCart(@RequestParam Integer productId, @AuthenticationPrincipal User user, @RequestHeader("Referer") String referer,
                      RedirectAttributes redirectAttributes) {
        try {
            Integer userId = user.getId();
            cartService.addCartItem(productId, userId);

            redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado!");
            return "redirect:" + referer;
        } catch (Exception e) {
            System.out.println(e);
            return "redirect:" + referer;
        }
    }
}
