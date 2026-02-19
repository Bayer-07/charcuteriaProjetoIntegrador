### 🏗 `model/`
Camada que define a estrutura de dados e a modelagem relacional.
* **Entidades**: Mapeamento direto das tabelas do banco de dados via JPA.
* **`dto/` (Data Transfer Objects)**: Objetos para tráfego de dados que protegem as entidades do banco, evitando o vazamento de informações sensíveis e desacoplando a API da persistência.
* **`enum/`**: Armazena constantes enumeradas para garantir a integridade dos tipos de dados (ex: Status de Pedido).