package com.example.charcuteria.unit.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.repository.cart.CartRepository;

@ExtendWith(MockitoExtension.class)
public class CartRepositoryTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CartRepository cartRepository;

    @Test
    void testGetProductQuantity_ReturnsTrue() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(10), eq(5)))
            .thenReturn(1);

        Boolean result = cartRepository.getProductQuantity(5, 10);

        assertTrue(result);
    }

    @Test
    void testGetProductQuantity_ReturnsFalse() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(10), eq(5)))
            .thenReturn(0);

        Boolean result = cartRepository.getProductQuantity(5, 10);

        assertFalse(result);
    }

    @Test
    void testGetProductQuantity_ReturnsFalseWhenNull() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(10), eq(5)))
            .thenReturn(null);

        Boolean result = cartRepository.getProductQuantity(5, 10);

        assertFalse(result);
    }

    @Test
    void testAddOneQuantity() {
        when(jdbcTemplate.update(anyString(), eq(10), eq(5))).thenReturn(1);

        Integer rows = cartRepository.addOneQuantity(5, 10);

        assertEquals(1, rows);
        verify(jdbcTemplate).update(anyString(), eq(10), eq(5));
    }

    @Test
    void testAddCartItem() {
        when(jdbcTemplate.update(anyString(), eq(10), eq(5))).thenReturn(1);

        Integer rows = cartRepository.addCartItem(5, 10);

        assertEquals(1, rows);
        verify(jdbcTemplate).update(anyString(), eq(10), eq(5));
    }

    @Test
    void testFindAllByUserId() {
        CartResponseDto mockItem = new CartResponseDto(1, 5, "Salaminho", "salaminho.png", new BigDecimal("35.00"), 2);
        
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<CartResponseDto>>any(), eq(10)))
            .thenReturn(Collections.singletonList(mockItem));

        List<CartResponseDto> result = cartRepository.findAllByUserId(10);

        assertEquals(1, result.size());
        assertEquals("Salaminho", result.get(0).productName());
        assertEquals(5, result.get(0).productId());
    }

    @Test
    void testGetCurrentlyQuantity() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(1)))
            .thenReturn(3);

        Integer quantity = cartRepository.getCurrentlyQuantity(1);

        assertEquals(3, quantity);
    }

    @Test
    void testUpdateCartQuantity() {
        cartRepository.updateCartQuantity(1, 5);

        verify(jdbcTemplate).update(anyString(), eq(5), eq(1));
    }

    @Test
    void testDeleteProductFromCart_Success() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(1);

        Boolean deleted = cartRepository.deleteProductFromCart(1);

        assertTrue(deleted);
    }

    @Test
    void testDeleteProductFromCart_Fail() {
        when(jdbcTemplate.update(anyString(), eq(1))).thenReturn(0);

        Boolean deleted = cartRepository.deleteProductFromCart(1);

        assertFalse(deleted);
    }
}