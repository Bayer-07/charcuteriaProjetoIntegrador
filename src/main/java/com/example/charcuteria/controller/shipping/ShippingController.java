package com.example.charcuteria.controller.shipping;

import com.example.charcuteria.dto.shipping.CepValidateRequest;
import com.example.charcuteria.dto.shipping.CepValidateResponse;
import com.example.charcuteria.dto.shipping.ShippingCalculateRequest;
import com.example.charcuteria.dto.shipping.ShippingCalculateResponse;
import com.example.charcuteria.model.User;
import com.example.charcuteria.service.shipping.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateShipping(
            @RequestBody ShippingCalculateRequest request,
            @AuthenticationPrincipal User user) {
        try {
            String cep = request.cep().replaceAll("\\D", "");

            if (cep.length() != 8) {
                return ResponseEntity.badRequest().body("CEP inválido");
            }

            Double price = shippingService.calculateShipping(cep, user.getId());
            return ResponseEntity.ok(new ShippingCalculateResponse(price));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Erro ao calcular frete");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<CepValidateResponse> validateCep(
            @RequestBody CepValidateRequest request) {
        String cep = request.cep().replaceAll("\\D", "");

        if (cep.length() != 8) {
            return ResponseEntity.badRequest().body(new CepValidateResponse(false, "CEP_INVALIDO"));
        }

        CepValidateResponse response = shippingService.validateCep(cep);
        return ResponseEntity.ok(response);
    }
}
