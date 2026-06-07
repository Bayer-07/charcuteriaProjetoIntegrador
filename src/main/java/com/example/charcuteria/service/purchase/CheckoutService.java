package com.example.charcuteria.service.purchase;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.charcuteria.dto.purchase.CheckoutRequest;
import com.example.charcuteria.repository.purchase.PurchaseRepository;
import com.example.charcuteria.repository.purchase.PurchaseRepository.CartItemDb;
import com.example.charcuteria.service.purchase.PixService.PixResult;
import com.example.charcuteria.service.shipping.ShippingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final PurchaseRepository orderRepository;
    private final PixService pixService;
    private final ShippingService shippingService;

    @Transactional(rollbackFor = Exception.class)
    public PixResult processCheckout(CheckoutRequest request, Integer userId) {

        List<CartItemDb> items = orderRepository.fetchCartItemsWithPricesAndStock(userId);
        if (items.isEmpty()) {
            log.warn("Tentativa de checkout com carrinho vazio pelo usuário ID: {}", userId);
            throw new IllegalStateException("O carrinho está vazio.");
        }

        String rawZipCode = orderRepository.fetchUserZipCode(request.addressId(), userId)
                .orElseThrow(() -> {
                    log.warn("Tentativa não autorizada de acesso a endereço. Address ID: {}, User ID: {}", request.addressId(), userId);
                    return new IllegalArgumentException("Endereço de entrega selecionado é inválido.");
                });

        String cleanCep = rawZipCode.replaceAll("\\D", "");

        Double shippingCostDouble = shippingService.calculateShipping(cleanCep, userId);
        if (shippingCostDouble == null || shippingCostDouble < 0) {
            throw new IllegalStateException("Não foi possível calcular o frete para a região selecionada.");
        }
        BigDecimal shippingCost = BigDecimal.valueOf(shippingCostDouble);

        BigDecimal subtotal = items.stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = subtotal.add(shippingCost);

        try {
            Integer orderId = orderRepository.createOrder(userId, request.addressId(), totalAmount, shippingCost);
            orderRepository.insertOrderProducts(orderId, items);

            orderRepository.deductStock(items);

            orderRepository.clearCart(userId);

            log.info("Checkout concluído com sucesso. User ID: {}. Order ID: {}.", userId, orderId);

            return pixService.generatePixForPurchase(totalAmount, orderId.longValue());

        } catch (DataAccessException e) {
            log.error("Transação de banco de dados rejeitada (possível bloqueio de trigger de estoque) para o usuário ID: {}", userId, e);
            throw new IllegalStateException("Desculpe, não temos estoque suficiente para um ou mais itens do seu carrinho. Revise as quantidades.");
        }
    }
}
