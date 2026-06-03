package com.example.charcuteria.controller.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.charcuteria.dto.address.AddressDtoRequest;
import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.address.AddressService;
import com.example.charcuteria.service.cart.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @PostMapping("/add")
    public String addCart(@RequestParam Integer productId, @AuthenticationPrincipal User user, @RequestHeader("Referer") String referer,
                      RedirectAttributes redirectAttributes) {
        try {
            Integer userId = user.getId();
            cartService.addCartItem(productId, userId);

            redirectAttributes.addFlashAttribute("successMessage", "Produto adicionado!");
            return "redirect:" + referer;
        } catch (Exception e) {
            return "redirect:" + referer;
        }
    }

    @GetMapping("")
    public String showCart(@AuthenticationPrincipal User loggedUser, Model model) {
        if (loggedUser == null) {
            return "redirect:/login";
        }

        List<CartResponseDto> cartItems = cartService.getAllCartItems(loggedUser.getId());
        List<Address> addresses = addressService.getAddressesByUserId(loggedUser.getId());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("addresses", addresses);
        model.addAttribute("addressDto", new AddressDtoRequest());

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

    @PostMapping(value = "/ajax/update-quantity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateQuantityAjax(@RequestParam Integer itemId, @RequestParam Integer delta) {
        try {
            cartService.updateItemQuantity(itemId, delta);

            return ResponseEntity.ok(Map.of("success", true, "delta", delta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProductFromCart(@PathVariable Integer id) {
        try {
            cartService.deleteProductFromCart(id);
            return "redirect:/cart";
        } catch (Exception e) {
            return "redirect:/cart";
        }
    }
}
