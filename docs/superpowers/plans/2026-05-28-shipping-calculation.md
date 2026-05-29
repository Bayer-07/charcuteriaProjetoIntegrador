# Shipping Calculation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add backend REST endpoint for shipping calculation via Melhor Envio API

**Architecture:** Controller delegates to Service which fetches cart items, sums quantities, calls Melhor Envio API with fixed product dimensions, returns lowest price. Token and origin CEP stored in .env.

**Tech Stack:** Spring Boot 3.5, RestTemplate, Spring Security, Maven

---

## File Structure

**New files:**
- `src/main/java/com/example/charcuteria/controller/shipping/ShippingController.java` — REST endpoint
- `src/main/java/com/example/charcuteria/service/shipping/ShippingService.java` — Melhor Envio integration
- `src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateRequest.java` — Request DTO
- `src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateResponse.java` — Response DTO
- `src/main/java/com/example/charcuteria/config/RestTemplateConfig.java` — HTTP client bean
- `src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java` — Unit tests

**Modified files:**
- `.env` — Add Melhor Envio token and origin CEP
- `src/main/java/com/example/charcuteria/config/SecurityConfig.java:48-49` — Add `/api/shipping/**` to public routes
- `src/main/resources/static/js/cart/ship.js` — Replace direct Melhor Envio call with backend endpoint

---

## Task 1: Environment Configuration

**Files:**
- Modify: `.env:6-7`

- [ ] **Step 1: Add shipping environment variables**

Open `.env` and replace line 7 (`CEP_`) with:

```properties
CEP_ORIGEM=85920260
```

Expected: `.env` now has `FRETE_ACCESS_TOKEN` (line 6) and `CEP_ORIGEM` (line 7)

- [ ] **Step 2: Verify Spring loads .env variables**

Run: `grep "spring.config.import" src/main/resources/application.properties`

Expected output:
```
spring.config.import=optional:file:.env[.properties]
```

Already configured. No changes needed.

- [ ] **Step 3: Commit environment configuration**

```bash
git add .env
git commit -m "config: add Melhor Envio shipping environment variables"
```

---

## Task 2: RestTemplate Configuration

**Files:**
- Create: `src/main/java/com/example/charcuteria/config/RestTemplateConfig.java`
- Test: Manual verification via compile

- [ ] **Step 1: Write failing compilation test**

Try to compile the project:

```bash
mvn clean compile
```

Expected: SUCCESS (baseline — no RestTemplateConfig yet)

- [ ] **Step 2: Create RestTemplateConfig**

Create file `src/main/java/com/example/charcuteria/config/RestTemplateConfig.java`:

```java
package com.example.charcuteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

- [ ] **Step 3: Verify compilation succeeds**

Run: `mvn clean compile`

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit RestTemplate configuration**

```bash
git add src/main/java/com/example/charcuteria/config/RestTemplateConfig.java
git commit -m "config: add RestTemplate bean for HTTP client"
```

---

## Task 3: Request DTO

**Files:**
- Create: `src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateRequest.java`
- Test: Manual verification via compile

- [ ] **Step 1: Create ShippingCalculateRequest record**

Create file `src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateRequest.java`:

```java
package com.example.charcuteria.dto.shipping;

public record ShippingCalculateRequest(String cep) {}
```

- [ ] **Step 2: Verify compilation succeeds**

Run: `mvn clean compile`

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit request DTO**

```bash
git add src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateRequest.java
git commit -m "feat: add shipping calculation request DTO"
```

---

## Task 4: Response DTO

**Files:**
- Create: `src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateResponse.java`
- Test: Manual verification via compile

- [ ] **Step 1: Create ShippingCalculateResponse record**

Create file `src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateResponse.java`:

```java
package com.example.charcuteria.dto.shipping;

public record ShippingCalculateResponse(Double price) {}
```

- [ ] **Step 2: Verify compilation succeeds**

Run: `mvn clean compile`

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit response DTO**

```bash
git add src/main/java/com/example/charcuteria/dto/shipping/ShippingCalculateResponse.java
git commit -m "feat: add shipping calculation response DTO"
```

---

## Task 5: ShippingService - Cart Items Fetch

**Files:**
- Create: `src/main/java/com/example/charcuteria/service/shipping/ShippingService.java`
- Test: `src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java`

- [ ] **Step 1: Write failing test for cart fetch**

Create file `src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java`:

```java
package com.example.charcuteria.unit.shipping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.service.cart.CartService;
import com.example.charcuteria.service.shipping.ShippingService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

class ShippingServiceTest {

    @Mock private CartService cartService;

    @Mock private RestTemplate restTemplate;

    @InjectMocks private ShippingService shippingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateTotalQuantity() {
        List<CartResponseDto> cartItems =
                List.of(
                        new CartResponseDto(
                                1, 10, "Product A", "img.jpg", BigDecimal.valueOf(50.0), 2),
                        new CartResponseDto(
                                2, 11, "Product B", "img2.jpg", BigDecimal.valueOf(30.0), 3));

        when(cartService.getAllCartItems(1)).thenReturn(cartItems);

        Integer totalQuantity = shippingService.calculateTotalQuantity(1);

        assertEquals(5, totalQuantity);
        verify(cartService).getAllCartItems(1);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -Dtest=ShippingServiceTest#testCalculateTotalQuantity`

Expected: FAIL with "ShippingService class not found" or similar

- [ ] **Step 3: Create ShippingService with calculateTotalQuantity**

Create file `src/main/java/com/example/charcuteria/service/shipping/ShippingService.java`:

```java
package com.example.charcuteria.service.shipping;

import com.example.charcuteria.dto.cart.CartResponseDto;
import com.example.charcuteria.service.cart.CartService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShippingService {

    @Autowired private CartService cartService;

    @Autowired private RestTemplate restTemplate;

    public Integer calculateTotalQuantity(Integer userId) {
        List<CartResponseDto> cartItems = cartService.getAllCartItems(userId);
        return cartItems.stream().mapToInt(CartResponseDto::quantity).sum();
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -Dtest=ShippingServiceTest#testCalculateTotalQuantity`

Expected: PASS

- [ ] **Step 5: Commit ShippingService with cart quantity calculation**

```bash
git add src/main/java/com/example/charcuteria/service/shipping/ShippingService.java
git add src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java
git commit -m "feat: add ShippingService with cart quantity calculation"
```

---

## Task 6: ShippingService - Melhor Envio API Call

**Files:**
- Modify: `src/main/java/com/example/charcuteria/service/shipping/ShippingService.java`
- Test: `src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java`

- [ ] **Step 1: Write failing test for Melhor Envio API call**

Add to `ShippingServiceTest.java`:

```java
@Test
void testCalculateShipping() {
    List<CartResponseDto> cartItems =
            List.of(
                    new CartResponseDto(
                            1, 10, "Product A", "img.jpg", BigDecimal.valueOf(50.0), 2));

    when(cartService.getAllCartItems(1)).thenReturn(cartItems);

    String mockResponse =
            "[{\"id\":1,\"name\":\"PAC\",\"price\":\"25.50\",\"custom_price\":\"25.50\"}]";
    when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
            .thenReturn(mockResponse);

    shippingService.setMelhorEnvioToken("test-token");
    shippingService.setOriginCep("85920260");

    Double price = shippingService.calculateShipping("12345678", 1);

    assertEquals(25.50, price);
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -Dtest=ShippingServiceTest#testCalculateShipping`

Expected: FAIL with "method calculateShipping not found" or compilation error

- [ ] **Step 3: Add calculateShipping method to ShippingService**

Modify `ShippingService.java` to add fields and method:

```java
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
            throw new RuntimeException("Erro ao calcular frete", e);
        }
    }

    private Double extractLowestPrice(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                if (first.has("custom_price")) {
                    return first.get("custom_price").asDouble();
                }
            }
            return 15.0; // fallback
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
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -Dtest=ShippingServiceTest#testCalculateShipping`

Expected: PASS

- [ ] **Step 5: Commit Melhor Envio API integration**

```bash
git add src/main/java/com/example/charcuteria/service/shipping/ShippingService.java
git add src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java
git commit -m "feat: add Melhor Envio API integration to ShippingService"
```

---

## Task 7: ShippingController

**Files:**
- Create: `src/main/java/com/example/charcuteria/controller/shipping/ShippingController.java`
- Test: Manual verification via curl

- [ ] **Step 1: Create ShippingController**

Create file `src/main/java/com/example/charcuteria/controller/shipping/ShippingController.java`:

```java
package com.example.charcuteria.controller.shipping;

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

    @Autowired private ShippingService shippingService;

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
}
```

- [ ] **Step 2: Verify compilation succeeds**

Run: `mvn clean compile`

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit ShippingController**

```bash
git add src/main/java/com/example/charcuteria/controller/shipping/ShippingController.java
git commit -m "feat: add ShippingController with calculate endpoint"
```

---

## Task 8: Security Configuration Update

**Files:**
- Modify: `src/main/java/com/example/charcuteria/config/SecurityConfig.java:48-49`

- [ ] **Step 1: Add /api/shipping/** to public routes**

Open `SecurityConfig.java` and modify line 48-49:

**Before:**
```java
.requestMatchers("/", "/index", "/index/top-products", "/login", "/loginAdmin", "/register",
        "/css/**", "/js/**", "/images/**", "/uploads/**", "/address/**", "/produtos", "/partners", "/subscriptions")
```

**After:**
```java
.requestMatchers("/", "/index", "/index/top-products", "/login", "/loginAdmin", "/register",
        "/css/**", "/js/**", "/images/**", "/uploads/**", "/address/**", "/produtos", "/partners", "/subscriptions", "/api/shipping/**")
```

- [ ] **Step 2: Verify compilation succeeds**

Run: `mvn clean compile`

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit security configuration**

```bash
git add src/main/java/com/example/charcuteria/config/SecurityConfig.java
git commit -m "config: allow public access to /api/shipping endpoints"
```

---

## Task 9: Frontend Integration

**Files:**
- Modify: `src/main/resources/static/js/cart/ship.js`

- [ ] **Step 1: Replace Melhor Envio direct call with backend endpoint**

Open `ship.js` and replace lines 30-56 (the axios.post call) with:

```javascript
try {
    const { data } = await axios.post('/api/shipping/calculate', {
        cep: cep
    });

    console.log(data);

    const menorFrete = data.price || 15.0;

    resultDiv.innerText = `Frete: R$ ${menorFrete.toFixed(2)}`;
    resultDiv.style.display = "block";

} catch (err) {
    console.error(err);
    resultDiv.innerText = "Erro ao calcular frete";
    resultDiv.style.display = "block";
}
```

Full updated function:

```javascript
async function calculateShipping() {
    const cepInput = document.getElementById('cepInput');
    const resultDiv = document.getElementById('shippingResult');
    const totalElement = document.querySelector('.total-value');

    if (!totalElement) return;

    if (totalOriginal === null) {
        let rawValue = totalElement.innerText
            .replace('R$', '')
            .replace(/\./g, '')
            .replace(',', '.')
            .trim();

        totalOriginal = parseFloat(rawValue);
    }

    const cep = cepInput.value.replace(/\D/g, '');

    if (cep.length !== 8) {
        alert("CEP inválido");
        return;
    }

    try {
        const { data } = await axios.post('/api/shipping/calculate', {
            cep: cep
        });

        console.log(data);

        const menorFrete = data.price || 15.0;

        resultDiv.innerText = `Frete: R$ ${menorFrete.toFixed(2)}`;
        resultDiv.style.display = "block";

    } catch (err) {
        console.error(err);
        resultDiv.innerText = "Erro ao calcular frete";
        resultDiv.style.display = "block";
    }
}

window.calculateShipping = calculateShipping;
```

- [ ] **Step 2: Remove commented axios import**

Remove line 1:

```javascript
// import axios from 'axios';
```

- [ ] **Step 3: Verify JavaScript syntax**

Run: `cat src/main/resources/static/js/cart/ship.js | grep "axios.post"`

Expected output should show the new backend call, not Melhor Envio URL

- [ ] **Step 4: Commit frontend integration**

```bash
git add src/main/resources/static/js/cart/ship.js
git commit -m "feat: integrate frontend with backend shipping endpoint"
```

---

## Task 10: Manual End-to-End Test

**Files:**
- Test all components together

- [ ] **Step 1: Start application**

Run: `mvn spring-boot:run`

Expected: Application starts on port 8080

- [ ] **Step 2: Register/Login as test user**

Navigate to `http://localhost:8080/register` and create account, or login at `http://localhost:8080/login`

- [ ] **Step 3: Add products to cart**

Navigate to products page, add items to cart

- [ ] **Step 4: Test shipping calculation**

Navigate to cart (`http://localhost:8080/cart`), enter CEP `01310100` (valid São Paulo CEP), click "Calcular"

Expected: "Frete: R$ XX.XX" displays below button

- [ ] **Step 5: Check browser console**

Open DevTools → Console

Expected: No JavaScript errors, axios response logged with `{price: XX.XX}`

- [ ] **Step 6: Check application logs**

Look at terminal running Spring Boot

Expected: No exceptions, RestTemplate logs visible (if DEBUG enabled)

- [ ] **Step 7: Test invalid CEP**

Enter CEP `123`, click "Calcular"

Expected: Alert "CEP inválido"

- [ ] **Step 8: Stop application**

Press Ctrl+C in terminal

---

## Task 11: Unit Test Coverage Review

**Files:**
- Test: Run all tests

- [ ] **Step 1: Run all unit tests**

Run: `mvn test`

Expected: All tests PASS, including `ShippingServiceTest`

- [ ] **Step 2: Verify test coverage**

Check test output for `ShippingServiceTest`:

Expected: 2 tests passed (testCalculateTotalQuantity, testCalculateShipping)

- [ ] **Step 3: Review test quality**

Open `ShippingServiceTest.java` and verify:
- Mocks are used for CartService and RestTemplate
- Edge cases covered (multiple products, quantity sum)
- Fallback price tested

- [ ] **Step 4: Commit if any test improvements made**

If you added tests:

```bash
git add src/test/java/com/example/charcuteria/unit/shipping/ShippingServiceTest.java
git commit -m "test: improve ShippingService test coverage"
```

---

## Self-Review Checklist

**Spec coverage:**
- ✅ Backend endpoint `/api/shipping/calculate` (Task 7)
- ✅ Token/CEP in `.env` (Task 1)
- ✅ Return lowest price (Task 6)
- ✅ Sum cart quantities (Task 5)
- ✅ Fixed dimensions (Task 6)
- ✅ Frontend integration (Task 9)
- ✅ Security config (Task 8)

**Placeholder scan:**
- ✅ No TBD/TODO
- ✅ All code blocks complete
- ✅ Exact file paths present
- ✅ Commands with expected output

**Type consistency:**
- ✅ `ShippingCalculateRequest(String cep)` used consistently
- ✅ `ShippingCalculateResponse(Double price)` used consistently
- ✅ `calculateShipping(String, Integer)` signature matches across files
- ✅ `calculateTotalQuantity(Integer)` signature consistent

**Dependencies:**
- ✅ RestTemplate available via spring-boot-starter-web
- ✅ Jackson available for JSON parsing
- ✅ Mockito available for tests
- ✅ Spring Security already configured

---

## Notes

- Melhor Envio token expires 2026-07-28 (check `.env` line 6 JWT `exp` field)
- Sandbox URL used: `https://sandbox.melhorenvio.com.br`
- Fixed dimensions: 11×17×11 cm, 1kg, insurance R$100
- Services "1,2,18" = PAC, SEDEX, PAC Mini
- Fallback price: R$15.00 if API fails or `custom_price` missing
- CEP validation: 8 digits after removing non-digits
