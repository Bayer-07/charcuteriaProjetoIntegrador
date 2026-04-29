# CRUD de Endereços - Documentação

## Endpoints Disponíveis

### CREATE (POST)
```
POST /api/addresses
Content-Type: application/json

{
  "userId": 1,
  "street": "Rua Principal",
  "number": "123",
  "complement": "Apto 45",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01234-567"
}
```

### READ (GET)
```
# Listar todos os endereços
GET /api/addresses

# Buscar endereço por ID
GET /api/addresses/{id}

# Listar endereços por usuário
GET /api/addresses/user/{userId}
```

### UPDATE (PUT)
```
PUT /api/addresses/{id}
Content-Type: application/json

{
  "userId": 1,
  "street": "Rua Nova",
  "number": "456",
  "complement": "Sala 10",
  "neighborhood": "Bairro Novo",
  "city": "Rio de Janeiro",
  "state": "RJ",
  "zipCode": "98765-432"
}
```

### DELETE (DELETE)
```
# Deletar endereço por ID
DELETE /api/addresses/{id}

# Deletar todos os endereços de um usuário
DELETE /api/addresses/user/{userId}
```

## Arquivos Implementados

1. **AddressController.java** - Endpoints REST
2. **AddressService.java** - Lógica de negócio
3. **AddressRepository.java** - Acesso ao banco (JdbcTemplate)
4. **Address.java** - Modelo/Entidade
5. **AddressDtoRequest.java** - DTO para requisições
6. **AddressDtoResponse.java** - DTO para respostas

## Fluxo de Dados

```
Request JSON 
  ↓
AddressController (recebe AddressDtoRequest)
  ↓
AddressService (processa lógica)
  ↓
AddressRepository (executa queries SQL)
  ↓
Banco de Dados
  ↓
Response em AddressDtoResponse
```

## Estrutura do Banco

Tabela esperada:
```sql
CREATE TABLE addresses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    street VARCHAR(255),
    number VARCHAR(20),
    complement VARCHAR(255),
    neighborhood VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(2),
    zip_code VARCHAR(10),
    is_default BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Exemplos de Uso (cURL)

### Criar endereço
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "street": "Rua A",
    "number": "100",
    "complement": "Casa",
    "neighborhood": "Bairro X",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01234-567"
  }'
```

### Listar todos
```bash
curl http://localhost:8080/api/addresses
```

### Buscar por ID
```bash
curl http://localhost:8080/api/addresses/1
```

### Buscar por usuário
```bash
curl http://localhost:8080/api/addresses/user/1
```

### Atualizar
```bash
curl -X PUT http://localhost:8080/api/addresses/1 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "street": "Rua B",
    "number": "200",
    "complement": "Apto 10",
    "neighborhood": "Bairro Y",
    "city": "Rio",
    "state": "RJ",
    "zipCode": "98765-432"
  }'
```

### Deletar
```bash
curl -X DELETE http://localhost:8080/api/addresses/1
```

### Deletar por usuário
```bash
curl -X DELETE http://localhost:8080/api/addresses/user/1
```
