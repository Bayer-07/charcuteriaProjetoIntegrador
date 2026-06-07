package com.example.charcuteria.dto.purchase;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
    @NotNull(message = "O ID do endereço de entrega é obrigatório")
    Long addressId
) {}
