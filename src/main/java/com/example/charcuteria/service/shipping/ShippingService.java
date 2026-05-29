package com.example.charcuteria.service.shipping;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.service.cart.CartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShippingService {

    @Autowired private CartService cartService;

    @Autowired private RestTemplate restTemplate;

    @Value("${FRETE_ACCESS_TOKEN}")
    private String melhorEnvioToken;

    @Value("${CEP_ORIGEM}")
    private String originCep;

    private static final String MELHOR_ENVIO_URL =
            "https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate";

    public Integer calculateTotalQuantity(Integer userId) {
        List<CartResponseDto> cartItems = cartService.getAllCartItems(userId);
        return cartItems.stream().mapToInt(CartResponseDto::quantity).sum();
    }

    public Double calculateShipping(String destinationCep, Integer userId) {
        System.out.println("=== SHIPPING CALCULATION ===");
        System.out.println("User ID: " + userId);
        System.out.println("Destination CEP: " + destinationCep);

        Integer totalQuantity = calculateTotalQuantity(userId);
        System.out.println("Total quantity from cart: " + totalQuantity);

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", Map.of("postal_code", originCep));
        payload.put("to", Map.of("postal_code", destinationCep));
        payload.put(
                "products",
                List.of(
                        Map.of(
                                "id", "Produto",
                                "width", 11,
                                "height", 17,
                                "length", 11,
                                "weight", 1,
                                "insurance_value", 100,
                                "quantity", totalQuantity)));
        payload.put("services", "1,2,18");

        System.out.println("Payload: " + payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "MinhaAplicacao (teste@email.com)");
        headers.set("Authorization", "Bearer " + melhorEnvioToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        System.out.println("=== REQUEST TO MELHOR ENVIO ===");
        System.out.println("URL: " + MELHOR_ENVIO_URL);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + payload);

        try {
            String response = restTemplate.postForObject(MELHOR_ENVIO_URL, request, String.class);
            System.out.println("=== MELHOR ENVIO RESPONSE ===");
            System.out.println(response);
            return extractLowestPrice(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao calcular frete", e);
        }
    }

    private Double extractLowestPrice(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            if (root.isArray() && root.size() > 0) {
                for (JsonNode service : root) {
                    // Pula serviços com erro
                    if (service.has("error")) {
                        continue;
                    }
                    // Pega primeiro serviço válido com custom_price
                    if (service.has("custom_price")) {
                        return service.get("custom_price").asDouble();
                    }
                }
            }
            return 15.0; // fallback se todos com erro
        } catch (Exception e) {
            return 15.0;
        }
    }

    // Test setters
    public void setMelhorEnvioToken(String token) {
        this.melhorEnvioToken = token;
    }

    public void setOriginCep(String cep) {
        this.originCep = cep;
    }
}
