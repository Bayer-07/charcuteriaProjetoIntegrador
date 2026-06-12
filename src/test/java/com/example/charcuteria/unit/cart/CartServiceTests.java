package com.example.charcuteria.unit.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ProductErrorCode;
import com.example.charcuteria.repository.cart.CartRepository;
import com.example.charcuteria.service.cart.CartService;

@ExtendWith(MockitoExtension.class)
public class CartServiceTests {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void testAddCartItem_ProductAlreadyInCart_IncrementsQuantity() {
        when(cartRepository.getProductQuantity(5, 10)).thenReturn(true);

        cartService.addCartItem(5, 10);

        verify(cartRepository).addOneQuantity(5, 10);
        verify(cartRepository, never()).addCartItem(anyInt(), anyInt());
    }

    @Test
    void testAddCartItem_NewProduct_Success() {
        when(cartRepository.getProductQuantity(5, 10)).thenReturn(false);
        when(cartRepository.addCartItem(5, 10)).thenReturn(1);

        cartService.addCartItem(5, 10);

        verify(cartRepository).addCartItem(5, 10);
        verify(cartRepository, never()).addOneQuantity(anyInt(), anyInt());
    }

    @Test
    void testAddCartItem_NewProduct_ThrowsBusinessException() {
        when(cartRepository.getProductQuantity(5, 10)).thenReturn(false);
        when(cartRepository.addCartItem(5, 10)).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            cartService.addCartItem(5, 10);
        });

        assertEquals(ProductErrorCode.PRODUCT_NOT_FOUND, exception.getProductErrorCode());
    }

    @Test
    void testGetAllCartItems() {
        CartResponseDto mockItem = new CartResponseDto(1, 5, "Salaminho", "salaminho.png", new BigDecimal("35.00"), 2);
        when(cartRepository.findAllByUserId(10)).thenReturn(Collections.singletonList(mockItem));

        List<CartResponseDto> result = cartService.getAllCartItems(10);

        assertEquals(1, result.size());
        assertEquals("Salaminho", result.get(0).productName());
        verify(cartRepository).findAllByUserId(10);
    }

    @Test
    void testUpdateItemQuantity_PositiveDelta() {
        when(cartRepository.getCurrentlyQuantity(1)).thenReturn(2);

        cartService.updateItemQuantity(1, 1);

        verify(cartRepository).updateCartQuantity(1, 3);
    }

    @Test
    void testUpdateItemQuantity_NegativeDelta_AboveZero() {
        when(cartRepository.getCurrentlyQuantity(1)).thenReturn(3);

        cartService.updateItemQuantity(1, -1);

        verify(cartRepository).updateCartQuantity(1, 2);
    }

    @Test
    void testUpdateItemQuantity_NegativeDelta_DropsBelowZero() {
        when(cartRepository.getCurrentlyQuantity(1)).thenReturn(1);

        cartService.updateItemQuantity(1, -2);

        verify(cartRepository).updateCartQuantity(1, 0);
    }

    @Test
    void testDeleteProductFromCart_Success() {
        when(cartRepository.deleteProductFromCart(1)).thenReturn(true);

        cartService.deleteProductFromCart(1);

        verify(cartRepository).deleteProductFromCart(1);
    }

    @Test
    void testDeleteProductFromCart_NotFound_ThrowsRuntimeException() {
        when(cartRepository.deleteProductFromCart(1)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.deleteProductFromCart(1);
        });

        assertEquals("produto nao encontrado no carrinho", exception.getMessage());
    }
}