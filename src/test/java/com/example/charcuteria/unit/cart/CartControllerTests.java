package com.example.charcuteria.unit.cart;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charcuteria.config.SecurityConfig;
import com.example.charcuteria.controller.cart.CartController;
import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.enums.UserRoleEnum;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.address.AddressService;
import com.example.charcuteria.service.cart.CartService;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
public class CartControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("gabriel@test.com");
        mockUser.setRole(UserRoleEnum.CUSTOMER);
    }

    @Test
    void testAddCart_Success() throws Exception {
        mockMvc.perform(post("/cart/add")
                .with(user(mockUser))
                .with(csrf())
                .header("Referer", "/products")
                .param("productId", "5"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/products"))
            .andExpect(flash().attribute("successMessage", "Produto adicionado!"));

        verify(cartService).addCartItem(5, 1);
    }

    @Test
    void testAddCart_Exception() throws Exception {
        doThrow(new RuntimeException("Out of stock"))
            .when(cartService).addCartItem(5, 1);

        mockMvc.perform(post("/cart/add")
                .with(user(mockUser))
                .with(csrf())
                .header("Referer", "/products")
                .param("productId", "5"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/products"));
    }

    @Test
    void testShowCart_NotLogged() throws Exception {
        mockMvc.perform(get("/cart"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testShowCart_LoggedWithItems() throws Exception {
        CartResponseDto item = new CartResponseDto(
            1, 
            105, 
            "Salaminho", 
            "salaminho.png", 
            new BigDecimal("35.00"), 
            2
        );
        List<CartResponseDto> cartItems = Collections.singletonList(item);
        List<Address> addresses = new ArrayList<>();

        when(cartService.getAllCartItems(1)).thenReturn(cartItems);
        when(addressService.getAddressesByUserId(1)).thenReturn(addresses);

        mockMvc.perform(get("/cart")
                .with(user(mockUser)))
            .andExpect(status().isOk())
            .andExpect(view().name("user/cart-view"))
            .andExpect(model().attribute("cartItems", cartItems))
            .andExpect(model().attribute("addresses", addresses))
            .andExpect(model().attributeExists("addressDto"))
            .andExpect(model().attribute("totalCart", new BigDecimal("70.00")));
    }

    @Test
    void testUpdateQuantity_Success() throws Exception {
        mockMvc.perform(post("/cart/update-quantity")
                .with(user(mockUser))
                .with(csrf())
                .param("itemId", "10")
                .param("delta", "1"))
            .andExpect(status().isOk());

        verify(cartService).updateItemQuantity(10, 1);
    }

    @Test
    void testUpdateQuantity_Exception() throws Exception {
        doThrow(new RuntimeException("Item not found"))
            .when(cartService).updateItemQuantity(10, 1);

        mockMvc.perform(post("/cart/update-quantity")
                .with(user(mockUser))
                .with(csrf())
                .param("itemId", "10")
                .param("delta", "1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateQuantityAjax_Success() throws Exception {
        mockMvc.perform(post("/cart/ajax/update-quantity")
                .with(user(mockUser))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("itemId", "10")
                .param("delta", "-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.delta").value(-1));

        verify(cartService).updateItemQuantity(10, -1);
    }

    @Test
    void testUpdateQuantityAjax_Exception() throws Exception {
        doThrow(new RuntimeException("Min quantity reached"))
            .when(cartService).updateItemQuantity(10, -1);

        mockMvc.perform(post("/cart/ajax/update-quantity")
                .with(user(mockUser))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("itemId", "10")
                .param("delta", "-1"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Min quantity reached"));
    }

    @Test
    void testDeleteProductFromCart_Success() throws Exception {
        mockMvc.perform(post("/cart/delete/15")
                .with(user(mockUser))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cart"));

        verify(cartService).deleteProductFromCart(15);
    }

    @Test
    void testDeleteProductFromCart_Exception() throws Exception {
        doThrow(new RuntimeException("Delete error"))
            .when(cartService).deleteProductFromCart(15);

        mockMvc.perform(post("/cart/delete/15")
                .with(user(mockUser))
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cart"));
    }
}