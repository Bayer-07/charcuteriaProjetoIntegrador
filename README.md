# Koch Charcuteria

> E-commerce especializado em produtos artesanais de charcutaria e embutidos premium.

---

# Sobre o Projeto

Este projeto consiste em um e-commerce voltado para a venda de produtos artesanais de charcutaria, oferecendo uma experiência moderna, elegante e intuitiva para os clientes.

A plataforma permite:

- Navegação por catálogo
- Compra online
- Gestão de estoque
- Área administrativa
- Experiência responsiva para mobile e desktop

---

# Objetivos

- Facilitar a venda online de produtos artesanais
- Melhorar a presença digital da marca
- Automatizar pedidos e estoque
- Criar uma experiência premium para os clientes

---

# Tecnologias Utilizadas

## Front-end

- Thymeleaf
- HTML
- CSS3
- Javascript

## Back-end

- Springboot
- PostgreSQL
- JDBC

## Outros

- Docker
- Spring security para autenticação

---

# Funcionalidades

## Usuários

- Cadastro
- Login
- Perfil do usuário
- Controle de plano

## Loja

- Catálogo de produtos
- Busca de produtos
- Filtros por categoria
- Carrinho de compras
- Checkout

## Administração

- Gestão de produtos
- Controle de estoque
- Gestão de pedidos
- Gestão de planos
- Dashboard administrativo

---

# Estrutura de Pastas

```bash
📦 charcuteria
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java
 ┃ ┃ ┃ ┣ 📂 config
 ┃ ┃ ┃ ┣ 📂 controller
 ┃ ┃ ┃ ┣ 📂 dto
 ┃ ┃ ┃ ┣ 📂 enums
 ┃ ┃ ┃ ┣ 📂 exceptions
 ┃ ┃ ┃ ┣ 📂 model
 ┃ ┃ ┃ ┣ 📂 repository
 ┃ ┃ ┃ ┗ 📂 services
 ┃ ┃ ┣ 📂 resources
 ┃ ┃ ┃ ┣ 📂 db/migrations
 ┃ ┃ ┃ ┣ 📂 static
 ┃ ┃ ┃ ┗ 📂 templates
 ┣ ┣ 📂 test
 ┃ ┃ ┣ 📂 integration
 ┃ ┃ ┗ 📂 unit
 ┣ 📜 .editorconfig
 ┣ 📜 .gitignore
 ┣ 📜 docker-compose.yaml
 ┣ 📜 mvnw
 ┣ 📜 pom.xlm
 ┗ 📜 README.md

```

# Rotas Principais

## Públicas

| Rota | Descrição |
|------|------------|
| `/` | Página inicial |
| `/login` | Login de usuários |
| `/register` | Cadastro de usuários |
| `/produtos` | Catálogo de produtos |
| `/cart` | Carrinho de compras |
| `/assinaturas` | Assinaturas |
| `/parcerias` | Parcerias |

---

## Usuário

| Rota | Descrição |
|------|------------|
| `/user/dashboard` | Perfil do usuário |
| `/user/subscriptions` | Assinaturas do usuário |
| `/user/orders` | Pedidos do usuário |

---

## Administração

| Rota | Descrição |
|------|------------|
| `/login/admin` | Login admin |
| `/admin/dashboard` | Dashboard administrativo |
| `/admin/products` | Gestão de produtos |
| `/admin/pedidos` | Gestão de pedidos |
| `/admin/assinaturas` | Gestão de assinaturas |

---

# Controle de Acesso

O sistema utiliza autenticação com Spring Security para controle de acesso baseado em permissões.

Tipos de acesso:

- Usuário comum
- Administrador

Rotas administrativas são protegidas e acessíveis apenas para usuários com perfil `ADMIN`.
