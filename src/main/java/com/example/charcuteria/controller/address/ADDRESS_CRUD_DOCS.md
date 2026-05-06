# CRUD de Endereços - Documentação Técnica

## Visão Geral

Documentação do CRUD de endereços

**Tipo**: Web Controller (Thymeleaf views, não REST API)  
**Autenticação**: Spring Security (`@AuthenticationPrincipal`)  
**Base URL**: `/addresses`

---

### Estrutura de Camadas

```
┌─────────────────────────────────────────┐
│ AddressController.java                  │
│ @Controller                             │
│ - Autenticação obrigatória              │
│ - Redirect + FlashAttributes            │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│ AddressService.java                     │
│ @Service                                │
│ - Conversão DTO → Entity                │
│ - Validação de existência               │
│ - Exceções de negócio                   │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│ AddressRepository.java                  │
│ @Repository                             │
│ - Queries SQL raw (JdbcTemplate)        │
│ - RowMapper customizado                 │
└─────────────────────────────────────────┘
```

### Modelos de Dados

**Address.java** (Entity)
```java
private Integer id;              // PK auto_increment
private Integer user_id;         // FK para users table
private String street;
private String number;
private String complement;       // opcional
private String neighborhood;
private String city;
private String state;            // sigla UF (2 chars)
private String zip_code;         // formato: 12345-678
private Boolean is_default;      // marca endereço principal
```

**AddressDtoRequest.java** (Input)
```java
// Todos campos obrigatórios exceto complement
private Integer user_id;
private String street;
private String number;
private String complement;      // opcional
private String neighborhood;
private String city;
private String state;
private String zip_code;
```

**AddressDtoResponse.java** (Output)
```java
// Construtor: AddressDtoResponse(Address address)
// Usado para serialização JSON (caso necessário no futuro)
private Integer id;
private Integer userId;         // camelCase
private String street;
private String number;
private String complement;
private String neighborhood;
private String city;
private String state;
private String zipCode;         // camelCase
private Boolean isDefault;      // camelCase
```

---

## Endpoints e Fluxos

### 1. Listar Endereços do Usuário Logado
**Rota**: `GET /addresses`

**Fluxo**:
```
1. Controller verifica autenticação
2. Extrai loggedUser.getId() do SecurityContext
3. AddressService.getAddressesByUserId(userId)
4. Repository: SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC
5. Model adiciona lista + email do usuário
6. Renderiza address/addresses-list.html
```

**Código**:
```java
// AddressController.java:32
@GetMapping
public String listAddresses(@AuthenticationPrincipal User loggedUser, Model model) {
    if (loggedUser == null) return "redirect:/login";
    
    List<Address> addresses = addressService.getAddressesByUserId(loggedUser.getId());
    model.addAttribute("addresses", addresses);
    model.addAttribute("userEmail", loggedUser.getEmail());
    return "address/addresses-list";
}
```

---

### 2. Formulário de Criação
**Rota**: `GET /addresses/new`

**Fluxo**:
```
1. Valida autenticação
2. Cria AddressDtoRequest vazio para bind do form
3. Adiciona userId ao model (hidden field)
4. Renderiza address/address-form.html
```

**Código**:
```java
// AddressController.java:45
@GetMapping("/new")
public String showCreateForm(@AuthenticationPrincipal User loggedUser, Model model) {
    if (loggedUser == null) return "redirect:/login";
    
    model.addAttribute("addressDto", new AddressDtoRequest());
    model.addAttribute("userId", loggedUser.getId());
    return "address/address-form";
}
```

---

### 3. Criar Endereço
**Rota**: `POST /addresses`

**Fluxo**:
```
1. Form submit com @ModelAttribute AddressDtoRequest
2. Controller injeta loggedUser.getId() no DTO (sobrescreve valor do form)
3. AddressService.createAddress(addressDto)
   3.1. Converte DTO → Address entity
   3.2. Chama repository.createAddress(address)
4. Repository: INSERT INTO addresses (...) VALUES (?, ..., TRUE)
   - is_default sempre TRUE na criação
5. RedirectAttributes com successMessage
6. Redirect para /addresses
```

**Código**:
```java
// AddressController.java:57
@PostMapping
public String createAddress(
        @ModelAttribute AddressDtoRequest addressDto,
        @AuthenticationPrincipal User loggedUser,
        RedirectAttributes redirectAttributes) {
    
    if (loggedUser == null) return "redirect:/login";
    
    try {
        addressDto.setUserId(loggedUser.getId()); // força userId correto
        addressService.createAddress(addressDto);
        redirectAttributes.addFlashAttribute("successMessage", "Endereço criado com sucesso!");
        return "redirect:/addresses";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar endereço: " + e.getMessage());
        return "redirect:/addresses/new";
    }
}

// AddressService.java:23
public void createAddress(AddressDtoRequest addressDto) {
    Address newAddress = new Address(
        addressDto.getUserId(),
        addressDto.getStreet(),
        addressDto.getNumber(),
        addressDto.getComplement(),
        addressDto.getNeighborhood(),
        addressDto.getCity(),
        addressDto.getState(),
        addressDto.getZipCode()
    );
    addressRepository.createAddress(newAddress);
}

// AddressRepository.java:35
public void createAddress(Address address) {
    String sql = "INSERT INTO addresses (user_id, street, number, complement, " +
                 "neighborhood, city, state, zip_code, is_default) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";
    
    jdbcTemplate.update(sql,
        address.getUserId(),
        address.getStreet(),
        address.getNumber(),
        address.getComplement(),
        address.getNeighborhood(),
        address.getCity(),
        address.getState(),
        address.getZipCode()
    );
}
```

**Validações**:
- ✅ Ownership: userId vem do SecurityContext (não confia no form)
- ⚠️ Input validation: não possui (sem @Valid)

---

### 4. Formulário de Edição
**Rota**: `GET /addresses/{id}/edit`

**Fluxo**:
```
1. Busca Address por ID
2. Valida existência (404 se não encontrado)
3. Valida ownership: address.userId == loggedUser.getId()
4. Converte Address → AddressDtoRequest (manual, sem mapper)
5. Adiciona DTO + addressId ao model
6. Renderiza address/address-form.html (mesmo form da criação)
```

**Código**:
```java
// AddressController.java:78
@GetMapping("/{id}/edit")
public String showEditForm(
        @PathVariable Integer id,
        @AuthenticationPrincipal User loggedUser,
        Model model,
        RedirectAttributes redirectAttributes) {
    
    if (loggedUser == null) return "redirect:/login";
    
    var address = addressService.getAddressById(id);
    
    if (address.isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Endereço não encontrado");
        return "redirect:/addresses";
    }
    
    Address foundAddress = address.get();
    if (!foundAddress.getUserId().equals(loggedUser.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage", 
            "Você não tem permissão para editar este endereço");
        return "redirect:/addresses";
    }
    
    // Conversão manual Address → DTO
    AddressDtoRequest dto = new AddressDtoRequest();
    dto.setUserId(foundAddress.getUserId());
    dto.setStreet(foundAddress.getStreet());
    dto.setNumber(foundAddress.getNumber());
    dto.setComplement(foundAddress.getComplement());
    dto.setNeighborhood(foundAddress.getNeighborhood());
    dto.setCity(foundAddress.getCity());
    dto.setState(foundAddress.getState());
    dto.setZipCode(foundAddress.getZipCode());
    
    model.addAttribute("addressDto", dto);
    model.addAttribute("addressId", id);
    return "address/address-form";
}
```

**Validações**:
- ✅ Ownership check (linhas 97-100)
- ✅ Existência (linhas 91-94)

---

### 5. Atualizar Endereço
**Rota**: `POST /addresses/{id}/edit`

**Fluxo**:
```
1. Form submit com @ModelAttribute + @PathVariable id
2. Valida autenticação + ownership (dupla verificação)
3. AddressService.updateAddress(id, addressDto)
   3.1. Busca address existente por ID
   3.2. Atualiza todos campos (exceto ID)
   3.3. Chama repository.updateAddress(id, address)
4. Repository: UPDATE addresses SET ... WHERE id = ?
5. Redirect com flash message
```

**Código**:
```java
// AddressController.java:119
@PostMapping("/{id}/edit")
public String updateAddress(
        @PathVariable Integer id,
        @ModelAttribute AddressDtoRequest addressDto,
        @AuthenticationPrincipal User loggedUser,
        RedirectAttributes redirectAttributes) {
    
    if (loggedUser == null) return "redirect:/login";
    
    try {
        var address = addressService.getAddressById(id);
        
        if (address.isEmpty() || !address.get().getUserId().equals(loggedUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Endereço não encontrado ou você não tem permissão");
            return "redirect:/addresses";
        }
        
        addressDto.setUserId(loggedUser.getId());
        addressService.updateAddress(id, addressDto);
        redirectAttributes.addFlashAttribute("successMessage", "Endereço atualizado com sucesso!");
        return "redirect:/addresses";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar endereço: " + e.getMessage());
        return "redirect:/addresses/" + id + "/edit";
    }
}

// AddressService.java:49
public void updateAddress(Integer id, AddressDtoRequest addressDto) {
    Optional<Address> existingAddress = addressRepository.findById(id);
    
    if (existingAddress.isPresent()) {
        Address address = existingAddress.get();
        address.setUSerId(addressDto.getUserId());
        address.setStreet(addressDto.getStreet());
        address.setNumber(addressDto.getNumber());
        address.setComplement(addressDto.getComplement());
        address.setNeighborhood(addressDto.getNeighborhood());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setZipCode(addressDto.getZipCode());
        
        addressRepository.updateAddress(id, address);
    } else {
        throw new RuntimeException("Endereço não encontrado com ID: " + id);
    }
}

// AddressRepository.java:71
public void updateAddress(Integer id, Address address) {
    String sql = "UPDATE addresses SET user_id = ?, street = ?, number = ?, " +
                 "complement = ?, neighborhood = ?, city = ?, state = ?, " +
                 "zip_code = ?, is_default = ? WHERE id = ?";
    
    jdbcTemplate.update(sql,
        address.getUserId(),
        address.getStreet(),
        address.getNumber(),
        address.getComplement(),
        address.getNeighborhood(),
        address.getCity(),
        address.getState(),
        address.getZipCode(),
        address.getIsDefault(),
        id
    );
}
```

**Problema de Segurança**:
- ⚠️ Service layer busca endereço 2x (controller já validou ownership)
- ⚠️ is_default pode ser sobrescrito sem controle

---

### 6. Deletar Endereço
**Rota**: `POST /addresses/{id}/delete`

**Fluxo**:
```
1. Valida autenticação
2. Busca address por ID
3. Valida ownership
4. AddressService.deleteAddress(id)
5. Repository: DELETE FROM addresses WHERE id = ?
6. Redirect com flash message
```

**Código**:
```java
// AddressController.java:148
@PostMapping("/{id}/delete")
public String deleteAddress(
        @PathVariable Integer id,
        @AuthenticationPrincipal User loggedUser,
        RedirectAttributes redirectAttributes) {
    
    if (loggedUser == null) return "redirect:/login";
    
    try {
        var address = addressService.getAddressById(id);
        
        if (address.isEmpty() || !address.get().getUserId().equals(loggedUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Endereço não encontrado ou você não tem permissão");
            return "redirect:/addresses";
        }
        
        addressService.deleteAddress(id);
        redirectAttributes.addFlashAttribute("successMessage", "Endereço deletado com sucesso!");
        return "redirect:/addresses";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar endereço: " + e.getMessage());
        return "redirect:/addresses";
    }
}

// AddressService.java:69
public void deleteAddress(Integer id) {
    addressRepository.deleteById(id);
}

// AddressRepository.java:88
public void deleteById(Integer id) {
    String sql = "DELETE FROM addresses WHERE id = ?";
    jdbcTemplate.update(sql, id);
}
```

**Validações**:
- ✅ Ownership check (linha 160)
- ✅ Soft delete não implementado (DELETE físico)

---

## Repository Pattern Details

### RowMapper Customizado
```java
// AddressRepository.java:20
private final RowMapper<Address> addressRowMapper = (rs, rowNum) -> {
    Address addr = new Address();
    addr.setId(rs.getInt("id"));
    addr.setUSerId(rs.getInt("user_id"));        // snake_case → camelCase
    addr.setStreet(rs.getString("street"));
    addr.setNumber(rs.getString("number"));
    addr.setComplement(rs.getString("complement"));
    addr.setNeighborhood(rs.getString("neighborhood"));
    addr.setCity(rs.getString("city"));
    addr.setState(rs.getString("state"));
    addr.setZipCode(rs.getString("zip_code"));   // snake_case → camelCase
    addr.setIsDefault(rs.getBoolean("is_default"));
    return addr;
};
```

### Métodos Adicionais (não usados pelo Controller)

**findAll()** - Busca todos endereços (sem filtro de usuário)
```java
// AddressRepository.java:66
public List<Address> findAll() {
    String sql = "SELECT * FROM addresses";
    return jdbcTemplate.query(sql, addressRowMapper);
}
```

**deleteByUserId()** - Deleta todos endereços de um usuário
```java
// AddressRepository.java:93
public void deleteByUserId(Integer userId) {
    String sql = "DELETE FROM addresses WHERE user_id = ?";
    jdbcTemplate.update(sql, userId);
}

// AddressService.java:73
public void deleteAddressesByUserId(Integer userId) {
    addressRepository.deleteByUserId(userId);
}
```

**Uso**: Cascata ao deletar usuário (não implementado ainda)

---

## Estrutura do Banco de Dados

```sql
CREATE TABLE addresses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20) NOT NULL,
    complement VARCHAR(255),          -- opcional
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,        -- sigla UF
    zip_code VARCHAR(10) NOT NULL,    -- formato: 12345-678
    is_default BOOLEAN DEFAULT TRUE,  -- endereço principal
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default)
);
```

**Observações**:
- `is_default`: sempre TRUE na criação (linha 36 do Repository)
- Sem constraint UNIQUE para is_default = TRUE por usuário
- created_at não é mapeado no modelo Address.java

---

## Pontos de Atenção

### ✅ Implementado Corretamente
1. **Autenticação obrigatória**: Todos endpoints verificam `@AuthenticationPrincipal`
2. **Ownership validation**: Usuário só acessa próprios endereços
3. **Flash messages**: Feedback UX via RedirectAttributes
4. **Ordenação**: findByUserId ordena por is_default DESC

### ⚠️ Melhorias Sugeridas

1. **Validação de Entrada**
```java
// Falta @Valid no DTO
@PostMapping
public String createAddress(@Valid @ModelAttribute AddressDtoRequest addressDto, 
                            BindingResult result) {
    if (result.hasErrors()) return "address/address-form";
    // ...
}
```

2. **Controle de is_default**
```java
// Garantir apenas 1 endereço default por usuário
@Transactional
public void setDefaultAddress(Integer addressId, Integer userId) {
    // Desmarcar todos
    jdbcTemplate.update("UPDATE addresses SET is_default = FALSE WHERE user_id = ?", userId);
    // Marcar o selecionado
    jdbcTemplate.update("UPDATE addresses SET is_default = TRUE WHERE id = ?", addressId);
}
```

3. **Injeção de Dependências**
```java
// Controller usa constructor injection (OK)
// Mas Repository poderia usar @RequiredArgsConstructor (Lombok)
@Repository
@RequiredArgsConstructor
public class AddressRepository {
    private final JdbcTemplate jdbcTemplate;
    // ...
}
```

4. **Exception Handling**
```java
// AddressService.java:65 lança RuntimeException genérica
// Criar exceção customizada
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(Integer id) {
        super("Endereço não encontrado com ID: " + id);
    }
}
```

5. **DTO Mapping**
```java
// Conversão manual Address → DTO (linhas 103-112 do Controller)
// Considerar ModelMapper ou MapStruct
@Component
public class AddressMapper {
    public AddressDtoRequest toDto(Address address) { /* ... */ }
    public Address toEntity(AddressDtoRequest dto) { /* ... */ }
}
```

---

## Testing Guide

### Teste de Integração (Controller)
```java
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user@test.com", roles = "USER")
class AddressControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldListUserAddresses() throws Exception {
        mockMvc.perform(get("/addresses"))
            .andExpect(status().isOk())
            .andExpect(view().name("address/addresses-list"))
            .andExpect(model().attributeExists("addresses"));
    }
    
    @Test
    void shouldPreventUnauthorizedEdit() throws Exception {
        // Tentar editar endereço de outro usuário
        mockMvc.perform(post("/addresses/999/edit")
                .param("street", "Hacked"))
            .andExpect(redirectedUrl("/addresses"))
            .andExpect(flash().attribute("errorMessage", 
                containsString("não tem permissão")));
    }
}
```

### Teste Unitário (Service)
```java
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    
    @Mock
    private AddressRepository addressRepository;
    
    @InjectMocks
    private AddressService addressService;
    
    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        when(addressRepository.findById(999)).thenReturn(Optional.empty());
        
        AddressDtoRequest dto = new AddressDtoRequest();
        dto.setStreet("Test");
        
        assertThrows(RuntimeException.class, 
            () -> addressService.updateAddress(999, dto));
    }
}
```

---

## Diagramas

### Sequência - Criar Endereço
```
User → Browser → Controller → Service → Repository → DB
 │        │           │           │          │         │
 │   POST /addresses  │           │          │         │
 │──────────────────→ │           │          │         │
 │        │       validate auth   │          │         │
 │        │       ────┐            │          │         │
 │        │           │            │          │         │
 │        │       ←───┘            │          │         │
 │        │      inject userId     │          │         │
 │        │       ────┐            │          │         │
 │        │       ←───┘            │          │         │
 │        │           │   createAddress()     │         │
 │        │           │───────────→│          │         │
 │        │           │    DTO→Entity         │         │
 │        │           │      ────┐ │          │         │
 │        │           │      ←───┘ │          │         │
 │        │           │            │  createAddress()   │
 │        │           │            │─────────→│         │
 │        │           │            │    INSERT │         │
 │        │           │            │          │────────→│
 │        │           │            │          │ ←───────│
 │        │           │            │←─────────│         │
 │        │           │←───────────│          │         │
 │        │      redirect:/addresses          │         │
 │   ←──────────────── │           │          │         │
 │   200 OK            │           │          │         │
```

### Estrutura de Pacotes
```
com.example.charcuteria
├── controller
│   └── address
│       └── AddressController.java      [Web Layer]
├── service
│   └── address
│       └── AddressService.java         [Business Logic]
├── repository
│   └── address
│       └── AddressRepository.java      [Data Access]
├── model
│   └── Address.java                    [Entity]
└── dto
    └── address
        ├── AddressDtoRequest.java      [Input DTO]
        └── AddressDtoResponse.java     [Output DTO]
```

---

## Troubleshooting

### Problema: "Endereço não encontrado" ao editar próprio endereço
**Causa**: Inconsistência entre user_id no Address e User logado  
**Solução**: Verificar SecurityContext e validar FK no banco

```sql
-- Debug query
SELECT a.*, u.email 
FROM addresses a 
JOIN users u ON a.user_id = u.id 
WHERE a.id = ?;
```

### Problema: Múltiplos endereços marcados como default
**Causa**: Falta constraint/trigger no banco  
**Solução temporária**: Fix manual
```sql
-- Resetar is_default para o mais recente
UPDATE addresses a1
SET is_default = (
    SELECT CASE WHEN a1.id = MAX(a2.id) THEN TRUE ELSE FALSE END
    FROM addresses a2
    WHERE a2.user_id = a1.user_id
)
WHERE user_id = ?;
```

### Problema: NullPointerException em getIsDefault()
**Causa**: Column `is_default` NULL no banco  
**Solução**:
```sql
UPDATE addresses SET is_default = TRUE WHERE is_default IS NULL;
ALTER TABLE addresses MODIFY is_default BOOLEAN NOT NULL DEFAULT TRUE;
```

---

## Referências

**Arquivos Relacionados**:
- [AddressController.java:1](src/main/java/com/example/charcuteria/controller/address/AddressController.java#L1)
- [AddressService.java:1](src/main/java/com/example/charcuteria/service/address/AddressService.java#L1)
- [AddressRepository.java:1](src/main/java/com/example/charcuteria/repository/address/AddressRepository.java#L1)
- [Address.java:1](src/main/java/com/example/charcuteria/model/Address.java#L1)
