# Charcuteria Backend

> Um Ecommerce para a charcuteria terceiro semestre eng de soft

## Tecnologias

- Jira -> https://grupo002.atlassian.net/jira/software/projects/G2/boards/1
- Github -> https://github.com/charcuteria/backend
- Linguagem/framework -> Aparentemente teremos que usar Java c/ springboot

### Prerequisites

- Ter o JDK 21 instalado

### Para clonar

```bash
git clone https://github.com/Bayer-07/charcuteriaProjetoIntegrador.git
```

### Para rodar

1- Subir o db

```bash
docker compose up (ou docker compose up -d para ser detached do terminal)
```

2- Rodar a application

```bash
./mvnw spring-boot:run
```

2- Rodar os testes

```bash
./mvnw test
```

### Jira things

- Para linkarmos o github com o jira, todas as branchs, commits e PRs tem que, obrigatóriamente, ter um código definido pelo Jira no COMEÇO.
- O código muda para cada issue criada no Jira.
