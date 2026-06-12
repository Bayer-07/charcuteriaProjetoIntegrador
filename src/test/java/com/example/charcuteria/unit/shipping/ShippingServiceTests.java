package com.example.charcuteria.unit.shipping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.dto.shipping.CepValidateResponse;
import com.example.charcuteria.dto.shipping.OpenCepResponse;
import com.example.charcuteria.service.cart.CartService;
import com.example.charcuteria.service.shipping.ShippingService;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTests {

    @Mock
    private CartService cartService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ShippingService shippingService;

    @BeforeEach
    void setUp() {
        shippingService.setOriginCep("85900000");
        shippingService.setMelhorEnvioToken("mock_token");
    }

    @Test
    void testCalculateTotalQuantity_ReturnsSum() {
        Integer userId = 1;
        List<CartResponseDto> mockCartItems = List.of(
            new CartResponseDto(1, 101, "Item A", "a.jpg", BigDecimal.TEN, 2),
            new CartResponseDto(2, 102, "Item B", "b.jpg", BigDecimal.TEN, 3)
        );

        when(cartService.getAllCartItems(userId)).thenReturn(mockCartItems);

        Integer total = shippingService.calculateTotalQuantity(userId);

        assertEquals(5, total);
        verify(cartService).getAllCartItems(userId);
    }

    @Test
    void testValidateCep_ValidPR() {
        String cep = "85900000";
        OpenCepResponse mockOpenCep = new OpenCepResponse(
            cep, "Rua Teste", "Apto 1", "Unidade", "Bairro", "Toledo", "PR", "Paraná", "Sul", "4128203"
        );

        when(restTemplate.getForObject(anyString(), eq(OpenCepResponse.class))).thenReturn(mockOpenCep);

        CepValidateResponse response = shippingService.validateCep(cep);

        assertTrue(response.valid());
        assertEquals("VALIDO", response.message());
    }

    @Test
    void testValidateCep_OutsidePR() {
        String cep = "01001000";
        OpenCepResponse mockOpenCep = new OpenCepResponse(
            cep, "Praça da Sé", "casarão", "Unidade", "Sé", "São Paulo", "SP", "São Paulo", "Sudeste", "3550308"
        );

        when(restTemplate.getForObject(anyString(), eq(OpenCepResponse.class))).thenReturn(mockOpenCep);

        CepValidateResponse response = shippingService.validateCep(cep);

        assertFalse(response.valid());
        assertEquals("FORA_PR", response.message());
    }

    @Test
    void testValidateCep_NullResponse_ReturnsInvalid() {
        String cep = "99999999";
        when(restTemplate.getForObject(anyString(), eq(OpenCepResponse.class))).thenReturn(null);

        CepValidateResponse response = shippingService.validateCep(cep);

        assertFalse(response.valid());
        assertEquals("CEP_INVALIDO", response.message());
    }

    @Test
    void testValidateCep_ExceptionThrown_ReturnsInvalid() {
        String cep = "00000000";
        when(restTemplate.getForObject(anyString(), eq(OpenCepResponse.class))).thenThrow(new RuntimeException());

        CepValidateResponse response = shippingService.validateCep(cep);

        assertFalse(response.valid());
        assertEquals("CEP_INVALIDO", response.message());
    }

    @Test
    void testCalculateShipping_SuccessWithLowestPrice() {
        Integer userId = 1;
        String destinationCep = "85900100";
        List<CartResponseDto> mockCartItems = List.of(
            new CartResponseDto(1, 101, "Item", "image.jpg", BigDecimal.TEN, 2)
        );
        String jsonResponse = "[{\"name\": \"Sedex\", \"error\": \"Erro\"}, {\"name\": \"Jadlog\", \"custom_price\": 22.40}]";

        when(cartService.getAllCartItems(userId)).thenReturn(mockCartItems);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class))).thenReturn(jsonResponse);

        Double price = shippingService.calculateShipping(destinationCep, userId);

        assertEquals(22.40, price);
    }

    @Test
    void testCalculateShipping_WithErrorsOnly_ReturnsFallback() {
        Integer userId = 1;
        String destinationCep = "85900100";
        List<CartResponseDto> mockCartItems = List.of(
            new CartResponseDto(1, 101, "Item", "image.jpg", BigDecimal.TEN, 1)
        );
        String jsonResponse = "[{\"name\": \"Sedex\", \"error\": \"Indisponível\"}]";

        when(cartService.getAllCartItems(userId)).thenReturn(mockCartItems);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class))).thenReturn(jsonResponse);

        Double price = shippingService.calculateShipping(destinationCep, userId);

        assertEquals(15.0, price);
    }

    @Test
    void testCalculateShipping_ApiFails_ThrowsRuntimeException() {
        Integer userId = 1;
        String destinationCep = "85900100";
        List<CartResponseDto> mockCartItems = Collections.emptyList();

        when(cartService.getAllCartItems(userId)).thenReturn(mockCartItems);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(String.class))).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            shippingService.calculateShipping(destinationCep, userId);
        });

        assertEquals("Erro ao calcular frete", exception.getMessage());
    }
}