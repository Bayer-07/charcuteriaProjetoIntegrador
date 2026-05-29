# Shipping Calculation Feature Design

**Date:** 2026-05-28  
**Status:** Approved

## Overview

Migrate shipping calculation from frontend to backend. Remove hardcoded Melhor Envio token from JavaScript and expose secure REST endpoint for freight calculation.

## Current State

- Frontend (`ship.js`) calls Melhor Envio API directly
- Token hardcoded in JavaScript (security risk)
- Fixed product dimensions: 11×17×11 cm, 1kg
- CEP origem hardcoded: `85920260`

## Requirements

1. Backend endpoint calculates shipping via Melhor Envio API
2. Token and CEP origem move to `.env`
3. Return only lowest price (not all services)
4. Sum cart quantities for `products[0].quantity` parameter
5. Keep fixed dimensions per product

## Architecture

### New Components

**Package structure:**
```
com.example.charcuteria.controller.shipping
  └── ShippingController

com.example.charcuteria.service.shipping
  └── ShippingService

com.example.charcuteria.dto.shipping
  ├── ShippingCalculateRequest
  └── ShippingCalculateResponse
```

### Endpoint

**Route:** `POST /api/shipping/calculate`

**Request:**
```json
{
  "cep": "12345678"
}
```

**Response:**
```json
{
  "price": 15.50
}
```

**Authentication:** userId extracted from `@AuthenticationPrincipal User` (Spring Security)

**Error cases:**
- 400: Invalid CEP format
- 404: User has no cart items
- 502: Melhor Envio API failure

### Data Flow

1. Frontend sends CEP to `/api/shipping/calculate`
2. `ShippingController` validates request, extracts userId from authenticated user
3. `ShippingService` fetches cart items via `CartService.getAllCartItems(userId)`
4. Service sums `quantity` from all `CartResponseDto` items
5. Service builds Melhor Envio payload:
   - `from.postal_code`: from `.env` (`SHIPPING_ORIGIN_CEP`)
   - `to.postal_code`: from request
   - `products[0]`: fixed dimensions × total quantity
6. Service calls Melhor Envio with `RestTemplate`
7. Service extracts `data[0].custom_price` or defaults to 15.00
8. Returns `ShippingCalculateResponse` with price

### Configuration

**New `.env` variables:**
```properties
MELHOR_ENVIO_TOKEN=eyJ0eXAiOiJKV1QiLCJhbGc...
SHIPPING_ORIGIN_CEP=85920260
```

**Access in Spring:**
```java
@Value("${melhor.envio.token}")
private String melhorEnvioToken;

@Value("${shipping.origin.cep}")
private String originCep;
```

### HTTP Client

Use `RestTemplate` (already available in Spring Boot) for Melhor Envio API calls.

**Configuration:**
```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### Components Detail

**ShippingController:**
- Single endpoint `POST /api/shipping/calculate`
- Validates CEP format (8 digits)
- Delegates to `ShippingService`
- Returns `ShippingCalculateResponse` JSON

**ShippingService:**
- Injected: `CartService`, `RestTemplate`
- Method: `calculateShipping(String cep, Integer userId)`
- Fetches cart items
- Sums quantities
- Calls Melhor Envio sandbox API
- Parses response, extracts lowest price

**ShippingCalculateRequest:**
```java
public record ShippingCalculateRequest(
    String cep
) {}
```

**ShippingCalculateResponse:**
```java
public record ShippingCalculateResponse(
    Double price
) {}
```

### Error Handling

- Invalid CEP: return 400 with message "CEP inválido"
- Empty cart: return 404 with message "Carrinho vazio"
- Melhor Envio API error: catch, log, return 502 with message "Erro ao calcular frete"
- Default fallback price: 15.00 if `custom_price` missing

### Frontend Changes

**ship.js modifications:**
1. Remove Melhor Envio API call
2. Replace with axios call to `/api/shipping/calculate`
3. Send `{ cep }` (userId from Spring Security session)
4. Handle response `{ price }`

**Before:**
```javascript
axios.post('https://sandbox.melhorenvio.com.br/...')
```

**After:**
```javascript
axios.post('/api/shipping/calculate', { cep })
```

### Testing Strategy

**Unit tests:**
- `ShippingService`: mock `CartService` and `RestTemplate`
- Verify quantity sum logic
- Verify Melhor Envio payload construction
- Verify price extraction and fallback

**Integration tests:**
- Mock Melhor Envio API response
- Verify end-to-end flow with test cart data

## Implementation Notes

- Fixed dimensions: width=11, height=17, length=11, weight=1
- Insurance value: 100 (fixed)
- Services: "1,2,18" (Melhor Envio service IDs)
- Sandbox URL: `https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate`
- Headers required: Accept, Content-Type, User-Agent, Authorization

## Out of Scope

- Multiple product dimension support
- Service selection (user picks carrier)
- Production Melhor Envio environment
- Shipping address persistence
- Freight display on cart total (frontend handles)

## Future Enhancements

- Add `width/height/length/weight` fields to `Product` model
- Calculate actual package dimensions based on cart items
- Support production Melhor Envio API
- Cache shipping calculations by CEP
- Track selected shipping service in order
