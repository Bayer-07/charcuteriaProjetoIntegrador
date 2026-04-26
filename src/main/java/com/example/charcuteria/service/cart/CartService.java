package com.example.charcuteria.service.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.exceptions.BusinessException;
import com.example.charcuteria.exceptions.ProductErrorCode;
import com.example.charcuteria.repository.cart.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public void addCartItem(Integer productId, Integer userId) {
        if (cartRepository.getProductQuantity(productId, userId) >= 1) {
            cartRepository.addOneQuantity(productId, userId);
            return;
        }
        if(cartRepository.addCartItem(productId, userId) == 0) throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
    }

    public List<CartResponseDto> getAllCartItems(Integer userId) {
        return cartRepository.findAllByUserId(userId);
    }
}
