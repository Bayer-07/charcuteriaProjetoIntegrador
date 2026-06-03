package com.example.charcuteria.unit.shipping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.service.cart.CartService;
import com.example.charcuteria.service.shipping.ShippingService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

class ShippingServiceTest {

    @Mock private CartService cartService;

    @Mock private RestTemplate restTemplate;

    @InjectMocks private ShippingService shippingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateTotalQuantity() {
        List<CartResponseDto> cartItems =
                List.of(
                        new CartResponseDto(
                                1, 10, "Product A", "img.jpg", BigDecimal.valueOf(50.0), 2),
                        new CartResponseDto(
                                2, 11, "Product B", "img2.jpg", BigDecimal.valueOf(30.0), 3));

        when(cartService.getAllCartItems(1)).thenReturn(cartItems);

        Integer totalQuantity = shippingService.calculateTotalQuantity(1);

        assertEquals(5, totalQuantity);
        verify(cartService).getAllCartItems(1);
    }

    @Test
    void testCalculateShipping() {
        List<CartResponseDto> cartItems =
                List.of(
                        new CartResponseDto(
                                1, 10, "Product A", "img.jpg", BigDecimal.valueOf(50.0), 2));

        when(cartService.getAllCartItems(1)).thenReturn(cartItems);

        String mockResponse =
                "[{\"id\":1,\"name\":\"PAC\",\"price\":\"25.50\",\"custom_price\":\"25.50\"}]";
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .thenReturn(mockResponse);

        shippingService.setMelhorEnvioToken("test-token");
        shippingService.setOriginCep("85920260");

        Double price = shippingService.calculateShipping("12345678", 1);

        assertEquals(25.50, price);
    }
}
