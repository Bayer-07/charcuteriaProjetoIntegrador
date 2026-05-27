# CRUD de Assinaturas - Documentação

## Endpoints Disponíveis

### SUBSCRIPTION PLANS (Planos de Assinatura)

#### CREATE (POST)
```
POST /admin/subscription-plans/new
Content-Type: application/x-www-form-urlencoded

Form Data:
- name: "Plano Mensal"
- description: "Assinatura mensal com entrega de produtos selecionados"
- price: 89.90
```

#### READ (GET)
```
# Listar todos os planos (Admin)
GET /admin/subscription-plans

# Buscar plano por ID (Admin)
GET /admin/subscription-plans/{id}
```

#### UPDATE (POST)
```
POST /admin/subscription-plans/update/{id}
Content-Type: application/x-www-form-urlencoded

Form Data:
- name: "Plano Mensal Premium"
- description: "Assinatura mensal premium com produtos exclusivos"
- price: 119.90
```

#### DELETE (POST)
```
# Deletar plano por ID
POST /admin/subscription-plans/delete/{id}
```

---

### SUBSCRIPTIONS (Assinaturas de Clientes)

#### CREATE (POST)
```
POST /subscriptions/new
Content-Type: application/x-www-form-urlencoded

Form Data:
- userId: 1
- planId: 2
- status: "ACTIVE"
- startedAt: "2024-01-15"
```

#### READ (GET)
```
# Listar todas as assinaturas do usuário logado
GET /subscriptions

# Buscar assinatura por ID
GET /subscriptions/{id}
```

#### UPDATE (POST)
```
POST /subscriptions/update/{id}
Content-Type: application/x-www-form-urlencoded

Form Data:
- userId: 1
- planId: 3
- status: "ACTIVE"
```

#### DELETE (POST)
```
# Deletar assinatura por ID
POST /subscriptions/delete/{id}
```

## Arquivos Implementados

### Controllers
1. **SubscriptionPlanController.java** - Gerenciamento de planos (Admin)
2. **SubscriptionController.java** - Gerenciamento de assinaturas (Cliente/Admin)

### Service Layer
3. **SubscriptionPlanService.java** - Lógica de negócio para planos
4. **SubscriptionService.java** - Lógica de negócio para assinaturas

### Repository
5. **SubscriptionPlanRepository.java** - Acesso ao banco (subscription_plans)
6. **SubscriptionRepository.java** - Acesso ao banco (subscriptions)

### Model & DTOs
7. **SubscriptionPlanRequest.java** - DTO para criação/edição de planos
8. **SubscriptionPlanResponse.java** - DTO para respostas de planos
9. **SubscriptionRequest.java** - DTO para criação/edição de assinaturas
10. **SubscriptionResponse.java** - DTO para respostas de assinaturas

## Fluxo de Dados

### Criação de Plano de Assinatura
```
Request (form-data)
  ↓
SubscriptionPlanController
  ↓
SubscriptionPlanService (valida dados, cria plano)
  ↓
SubscriptionPlanRepository (INSERT SQL)
  ↓
Banco de Dados (subscription_plans)
```

### Criação de Assinatura
```
Request (form-data)
  ↓
SubscriptionController
  ↓
SubscriptionService (valida userId, planId, cria assinatura)
  ↓
SubscriptionRepository (INSERT SQL)
  ↓
Banco de Dados (subscriptions)
```

### Atualização de Assinatura
```
Request (form-data com id)
  ↓
SubscriptionController
  ↓
SubscriptionService (valida novos dados)
  ↓
SubscriptionRepository (UPDATE SQL)
  ↓
Banco de Dados
```

### Exclusão de Assinatura
```
Request DELETE
  ↓
SubscriptionController
  ↓
SubscriptionService (verifica existência)
  ↓
SubscriptionRepository (DELETE SQL)
  ↓
Banco de Dados
```

## Estrutura do Banco

Tabelas esperadas:
```sql
CREATE TABLE subscription_plans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE subscriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    plan_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at DATE NOT NULL DEFAULT CURRENT_DATE,
    
    CONSTRAINT fk_user_subscription FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_plan_subscription FOREIGN KEY (plan_id) 
        REFERENCES subscription_plans(id)
);
```

## Exemplos de Uso (cURL)

### Criar plano de assinatura
```bash
curl -X POST http://localhost:8080/admin/subscription-plans/new \
  -d "name=Plano Mensal" \
  -d "description=Assinatura mensal com produtos selecionados" \
  -d "price=89.90"
```

### Listar todos os planos (Admin)
```bash
curl http://localhost:8080/admin/subscription-plans
```

### Buscar plano por ID (Admin)
```bash
curl http://localhost:8080/admin/subscription-plans/1
```

### Atualizar plano
```bash
curl -X POST http://localhost:8080/admin/subscription-plans/update/1 \
  -d "name=Plano Mensal Premium" \
  -d "description=Assinatura premium com produtos exclusivos" \
  -d "price=119.90"
```

### Deletar plano
```bash
curl -X POST http://localhost:8080/admin/subscription-plans/delete/1
```

### Criar assinatura (usuário logado)
```bash
curl -X POST http://localhost:8080/subscriptions/new \
  -d "userId=1" \
  -d "planId=2" \
  -d "status=ACTIVE" \
  -d "startedAt=2024-01-15"
```

### Listar assinaturas do usuário logado
```bash
curl http://localhost:8080/subscriptions \
  -H "Authorization: Bearer <token>"
```

### Buscar assinatura por ID
```bash
curl http://localhost:8080/subscriptions/1
```

### Atualizar assinatura
```bash
curl -X POST http://localhost:8080/subscriptions/update/1 \
  -d "userId=1" \
  -d "planId=3" \
  -d "status=ACTIVE"
```

### Deletar assinatura
```bash
curl -X POST http://localhost:8080/subscriptions/delete/1
```

## Diferenças entre Admin e Cliente

### SubscriptionPlanController (`/admin/subscription-plans`)
- Gerenciamento completo de planos de assinatura
- Criação, edição e exclusão de planos
- Definição de preços e descrições
- Controle de planos ativos/inativos (`is_active`)
- Acesso restrito a administradores

### SubscriptionController (`/subscriptions`)
- Gerenciamento de assinaturas do usuário
- Criação de novas assinaturas (vínculo usuário-plano)
- Visualização de assinaturas próprias
- Atualização de status (ACTIVE, INACTIVE, CANCELLED)
- Cancelamento de assinaturas
- Requer autenticação do usuário

## Observações Importantes

1. **Relacionamentos**: 
   - `subscriptions.user_id` → FK para `users.id`
   - `subscriptions.plan_id` → FK para `subscription_plans.id`
   
2. **Status de Assinatura**: Valores possíveis incluem:
   - `ACTIVE` - Assinatura ativa
   - `INACTIVE` - Assinatura inativa
   - `CANCELLED` - Assinatura cancelada

3. **Controle de Acesso**: 
   - SubscriptionPlanController requer role ADMIN
   - SubscriptionController requer autenticação do usuário
   - Usuários só podem visualizar suas próprias assinaturas

4. **Data de Início**: Campo `started_at` registra quando assinatura foi iniciada

5. **Soft Delete em Planos**: Campo `is_active` em subscription_plans permite desativar planos sem deletar

6. **Cascade Delete**: Exclusão de usuário remove suas assinaturas automaticamente (ON DELETE CASCADE)

7. **Validação**: Controllers usam `@ModelAttribute` e `@AuthenticationPrincipal` para validação e segurança
