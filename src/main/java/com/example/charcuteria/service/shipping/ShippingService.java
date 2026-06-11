package com.example.charcuteria.service.shipping;

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

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.dto.shipping.CepValidateResponse;
import com.example.charcuteria.dto.shipping.OpenCepResponse;
import com.example.charcuteria.service.cart.CartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShippingService {

    @Autowired private CartService cartService;

    @Autowired private RestTemplate restTemplate;

    @Value("${FRETE_ACCESS_TOKEN:}")
    private String melhorEnvioToken;

    @Value("${CEP_ORIGEM:}")
    private String originCep;

    private static final String MELHOR_ENVIO_URL =
            "https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate";

    private static final String OPEN_CEP_URL = "https://opencep.com/v1/";

    public Integer calculateTotalQuantity(Integer userId) {
        List<CartResponseDto> cartItems = cartService.getAllCartItems(userId);
        return cartItems.stream().mapToInt(CartResponseDto::quantity).sum();
    }

    public CepValidateResponse validateCep(String cep) {
        try {
            String url = OPEN_CEP_URL + cep + ".json";
            OpenCepResponse response = restTemplate.getForObject(url, OpenCepResponse.class);

            if (response == null) {
                return new CepValidateResponse(false, "CEP_INVALIDO");
            }

            boolean isValidUf = "PR".equals(response.uf());
            String message = isValidUf ? "VALIDO" : "FORA_PR";
            return new CepValidateResponse(isValidUf, message);

        } catch (Exception e) {
            return new CepValidateResponse(false, "CEP_INVALIDO");
        }
    }

    public Double calculateShipping(String destinationCep, Integer userId) {
        Integer totalQuantity = calculateTotalQuantity(userId);

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "MinhaAplicacao (teste@email.com)");
        headers.set("Authorization", "Bearer " + melhorEnvioToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            String response = restTemplate.postForObject(MELHOR_ENVIO_URL, request, String.class);
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
