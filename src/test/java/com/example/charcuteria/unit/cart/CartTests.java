package com.example.charcuteria.unit.cart;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.cart.CartController;
import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.address.AddressService;
import com.example.charcuteria.service.cart.CartService;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private AddressService addressService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("user@test.com");
        testUser.setRole(UserRoleEnum.CUSTOMER);

        when(addressService.getAddressesByUserId(1)).thenReturn(Collections.emptyList());
    }

    @Test
    void testAddCart_Success() throws Exception {
        mockMvc.perform(post("/cart/add")
                .with(user(testUser))
                .param("productId", "10")
                .header("Referer", "/products"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attributeExists("successMessage"))
            .andExpect(redirectedUrl("/products"));

        verify(cartService).addCartItem(10, 1);
    }

    @Test
    void testAddCart_Exception() throws Exception {
        doThrow(new RuntimeException("Erro ao adicionar")).when(cartService).addCartItem(anyInt(), anyInt());

        mockMvc.perform(post("/cart/add")
                .with(user(testUser))
                .param("productId", "10")
                .header("Referer", "/products"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/products"));
    }

    @Test
    void testShowCart_WithItems() throws Exception {
        List<CartResponseDto> cartItems = Arrays.asList(
            new CartResponseDto(1, 10, "Produto 1", "image1.jpg", new BigDecimal("15.50"), 2),
            new CartResponseDto(2, 11, "Produto 2", "image2.jpg", new BigDecimal("20.00"), 1)
        );

        when(cartService.getAllCartItems(1)).thenReturn(cartItems);

        mockMvc.perform(get("/cart")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("user/cart-view"))
            .andExpect(model().attributeExists("cartItems"))
            .andExpect(model().attributeExists("totalCart"))
            .andExpect(model().attribute("totalCart", new BigDecimal("51.00")));
    }

    @Test
    void testShowCart_Empty() throws Exception {
        when(cartService.getAllCartItems(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cart")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("user/cart-view"))
            .andExpect(model().attribute("totalCart", BigDecimal.ZERO));
    }

    @Test
    void testUpdateQuantity_Success() throws Exception {
        mockMvc.perform(post("/cart/update-quantity")
                .with(user(testUser))
                .param("itemId", "5")
                .param("delta", "1"))
            .andExpect(status().isOk());

        verify(cartService).updateItemQuantity(5, 1);
    }

    @Test
    void testUpdateQuantity_Decrease() throws Exception {
        mockMvc.perform(post("/cart/update-quantity")
                .with(user(testUser))
                .param("itemId", "5")
                .param("delta", "-1"))
            .andExpect(status().isOk());

        verify(cartService).updateItemQuantity(5, -1);
    }

    @Test
    void testUpdateQuantity_Exception() throws Exception {
        doThrow(new RuntimeException("Erro ao atualizar")).when(cartService).updateItemQuantity(anyInt(), anyInt());

        mockMvc.perform(post("/cart/update-quantity")
                .with(user(testUser))
                .param("itemId", "5")
                .param("delta", "1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteProductFromCart_Success() throws Exception {
        mockMvc.perform(post("/cart/delete/5")
                .with(user(testUser)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cart"));

        verify(cartService).deleteProductFromCart(5);
    }

    @Test
    void testDeleteProductFromCart_Exception() throws Exception {
        doThrow(new RuntimeException("Produto não encontrado")).when(cartService).deleteProductFromCart(anyInt());

        mockMvc.perform(post("/cart/delete/5")
                .with(user(testUser)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cart"));
    }

    // Alta prioridade - Edge cases
    @Test
    void testAddCart_Unauthenticated_Redirects() throws Exception {
        mockMvc.perform(post("/cart/add")
                .param("productId", "10")
                .header("Referer", "/products"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testUpdateQuantity_ToZero() throws Exception {
        mockMvc.perform(post("/cart/update-quantity")
                .with(user(testUser))
                .param("itemId", "5")
                .param("delta", "-10"))
            .andExpect(status().isOk());

        verify(cartService).updateItemQuantity(5, -10);
    }

    @Test
    void testShowCart_MultipleProducts_CorrectTotal() throws Exception {
        List<CartResponseDto> cartItems = Arrays.asList(
            new CartResponseDto(1, 10, "Produto A", "img1.jpg", new BigDecimal("10.50"), 3),
            new CartResponseDto(2, 11, "Produto B", "img2.jpg", new BigDecimal("25.75"), 2),
            new CartResponseDto(3, 12, "Produto C", "img3.jpg", new BigDecimal("5.99"), 5)
        );

        when(cartService.getAllCartItems(1)).thenReturn(cartItems);

        BigDecimal expectedTotal = new BigDecimal("10.50").multiply(new BigDecimal("3"))
            .add(new BigDecimal("25.75").multiply(new BigDecimal("2")))
            .add(new BigDecimal("5.99").multiply(new BigDecimal("5")));

        mockMvc.perform(get("/cart")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("user/cart-view"))
            .andExpect(model().attribute("totalCart", expectedTotal));
    }
}
