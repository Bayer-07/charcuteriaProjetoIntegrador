# CRUD de Produtos - Documentação

## Endpoints Disponíveis

### CREATE (POST)
```
POST /admin/product/create
Content-Type: multipart/form-data

Form Data:
- name: "Queijo Minas"
- description: "Queijo artesanal produzido em Minas Gerais"
- category: "Queijos"
- price: 45.90
- stock: 50
- image: (arquivo de imagem)
```

### READ (GET)
```
# Listar todos os produtos (página pública - catálogo)
GET /produtos

# Buscar produto por ID (Admin)
GET /admin/product/{id}

# Buscar produto por ID (Cliente)
GET /produtos/{id}
```

### UPDATE (POST)
```
POST /admin/product/update
Content-Type: multipart/form-data

Form Data:
- id: 1
- name: "Queijo Minas Especial"
- description: "Queijo artesanal premium de Minas Gerais"
- category: "Queijos"
- price: 52.90
- stock: 30
- file: (arquivo de imagem - opcional, mantém anterior se não enviado)
```

### DELETE (POST)
```
# Deletar produto por ID (soft delete - is_active = FALSE)
POST /admin/product/delete/{id}
```

## Arquivos Implementados

### Controllers
1. **AdminProductController.java** - Endpoints administrativos (CRUD completo)
2. **CustomerProductController.java** - Endpoints públicos (somente leitura)

### Service Layer
3. **ProductService.java** - Lógica de negócio
4. **FileStorageService.java** - Gerenciamento de upload/exclusão de imagens

### Repository
5. **ProductRepository.java** - Acesso ao banco (JdbcTemplate)

### Model & DTOs
6. **Product.java** - Modelo/Entidade
7. **ProductsRequestDto.java** - DTO para criação (com upload de imagem)
8. **ProductsEditRequestDto.java** - DTO para edição (com upload opcional)
9. **ProductsEditResponseDto.java** - DTO para respostas de edição
10. **ProductCatalogDto.java** - DTO para listagem pública (catálogo)

## Fluxo de Dados

### Criação de Produto
```
Request (multipart/form-data)
  ↓
AdminProductController
  ↓
FileStorageService (salva imagem → retorna nome do arquivo)
  ↓
ProductService (busca categoryId, cria produto)
  ↓
ProductRepository (INSERT SQL)
  ↓
Banco de Dados
```

### Atualização de Produto
```
Request (multipart/form-data)
  ↓
AdminProductController
  ↓
ProductService (busca nome arquivo atual)
  ↓
FileStorageService (se nova imagem: salva nova, deleta antiga)
  ↓
ProductRepository (UPDATE SQL)
  ↓
Banco de Dados
```

### Exclusão de Produto (Soft Delete)
```
Request DELETE
  ↓
AdminProductController
  ↓
ProductService (busca nome do arquivo de imagem)
  ↓
ProductRepository (UPDATE is_active = FALSE, image_path = NULL)
  ↓
FileStorageService (deleta arquivo físico)
  ↓
Banco de Dados
```

## Estrutura do Banco

Tabela esperada:
```sql
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    image_path VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
```

## Exemplos de Uso (cURL)

### Criar produto
```bash
curl -X POST http://localhost:8080/admin/product/create \
  -F "name=Queijo Minas" \
  -F "description=Queijo artesanal de Minas Gerais" \
  -F "category=Queijos" \
  -F "price=45.90" \
  -F "stock=50" \
  -F "image=@/caminho/para/imagem.jpg"
```

### Listar todos os produtos (catálogo público)
```bash
curl http://localhost:8080/produtos
```

### Buscar produto por ID (Admin)
```bash
curl http://localhost:8080/admin/product/1
```

### Buscar produto por ID (Cliente)
```bash
curl http://localhost:8080/produtos/1
```

### Atualizar produto
```bash
curl -X POST http://localhost:8080/admin/product/update \
  -F "id=1" \
  -F "name=Queijo Minas Premium" \
  -F "description=Queijo artesanal premium" \
  -F "category=Queijos" \
  -F "price=52.90" \
  -F "stock=30" \
  -F "file=@/caminho/para/nova-imagem.jpg"
```

### Deletar produto (soft delete)
```bash
curl -X POST http://localhost:8080/admin/product/delete/1
```

## Diferenças entre Admin e Cliente

### AdminProductController (`/admin/product`)
- Criação de produtos com upload de imagem
- Atualização completa (incluindo substituição de imagem)
- Exclusão (soft delete com remoção de arquivo)
- Acesso total aos dados do produto

### CustomerProductController (`/produtos`)
- Somente leitura
- Lista produtos ativos (`is_active = TRUE`)
- Endpoint público para catálogo
- Acesso aos detalhes do produto por ID

## Observações Importantes

1. **Soft Delete**: A exclusão não remove o registro do banco, apenas marca `is_active = FALSE` e remove o caminho da imagem
2. **Upload de Imagens**: Gerenciado pelo `FileStorageService`, que retorna o nome do arquivo salvo
3. **Categoria**: Relacionamento via `category_id` (FK para tabela `categories`)
4. **Validação**: Controllers usam `@Valid` e `BindingResult` para validação de entrada
5. **Exception Handling**: Service lança `BusinessException` com `ProductErrorCode` quando operação falha
