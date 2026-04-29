package com.example.charcuteria.controller.cart;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.dto.cart.CartResponseDto;
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

    @GetMapping("")
    public String showCart(@AuthenticationPrincipal User loggedUser, Model model) {
        List<CartResponseDto> cartItems = cartService.getAllCartItems(loggedUser.getId());
        model.addAttribute("cartItems", cartItems);

        BigDecimal totalFinal = cartItems.stream()
            .map(CartResponseDto::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalCart", totalFinal);
        return "user/cart-view";
    }

    @PostMapping("/update-quantity")
    @ResponseBody
    public Object updateQuantity(@RequestParam Integer itemId, @RequestParam Integer delta) {
        try {
            cartService.updateItemQuantity(itemId, delta);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
