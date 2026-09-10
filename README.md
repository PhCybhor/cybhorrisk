# Cybhor Risk

API didática de **gerenciadora de risco**, desenvolvida no padrão da disciplina de Programação (Spring Boot 2026), a partir do projeto de referência [suporteos2026](https://github.com/jeffersonarpasserini/suporteos2026).

## Domínio

- Categoria de risco: classificação dos eventos (tecnológico, operacional, conformidade).
- Risco: evento identificado por código único, com probabilidade e impacto financeiro.
- Área responsável: setor opcional que acompanha o risco.

## Requisitos

- Java 21
- Git
- IntelliJ IDEA (Ultimate é o caminho principal do curso; o Spring Initializr também funciona)
- Docker Desktop, utilizado nas aulas de PostgreSQL e containerização

## Correspondência com o curso

O `suporteos2026` demonstra controle de produtos por grupos. Este projeto mantém a mesma estrutura conceitual, trocando o tema:

```text
GrupoProduto  -> CategoriaRisco
Produto       -> Risco
Fornecedor    -> AreaResponsavel
```

- [Consultar o tema do projeto](docs/tema-do-projeto.md)

## Executando o projeto

Na primeira execução, copie `.env.example` para `.env`, preencha `DB_DEV_PASSWORD` e `DB_TEST_PASSWORD` e mantenha esse arquivo fora do Git.

Com Docker Desktop:

```powershell
Copy-Item .env.example .env
docker compose up -d
```

O `docker-compose.yml` sobe o PostgreSQL e cria os bancos `cybhorrisco_dev`, `cybhorrisco_test`, `cybhorrisco_diff` e `cybhorrisco_reference`, com o usuário `cybhor_app`. Sem Docker, crie esses bancos manualmente.

No Windows:

```powershell
Copy-Item .env.example .env
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

No macOS ou Linux:

```bash
cp .env.example .env
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Com a aplicação iniciada, acesse <http://localhost:8080/api/health>. A resposta esperada é `OK`.

## Modelo de domínio

```text
CategoriaRisco 1 ─────── N Risco
AreaResponsavel  1 ─────── N Risco
```

A área responsável de um risco é opcional. As classes estão no pacote `com.curso.cybhorrisco.domain`, são mapeadas com JPA e preservam as regras de negócio. O Liquibase cria e versiona o esquema; o Hibernate apenas o valida. Os profiles `dev`, `test` e `prod` usam PostgreSQL, sem H2.

## API

Após iniciar a aplicação, estão disponíveis os cadastros, consultas por ID e listagens de:

- `http://localhost:8080/api/categorias-risco`
- `http://localhost:8080/api/areas-responsaveis`
- `http://localhost:8080/api/riscos`

Os contratos usam DTOs, validação de entrada e respostas de erro padronizadas.

### Exemplo de cadastro de categoria

```json
POST /api/categorias-risco
{
  "nome": "Tecnológico"
}
```

### Exemplo de cadastro de área

```json
POST /api/areas-responsaveis
{
  "nome": "Tecnologia da Informação",
  "sigla": "TI"
}
```

### Exemplo de cadastro de risco

```json
POST /api/riscos
{
  "codigo": "RSK-0001",
  "descricao": "Indisponibilidade do sistema de autenticação",
  "probabilidade": 20.000,
  "impactoFinanceiro": 15000.00,
  "limiteTolerancia": 5000.00,
  "categoriaId": 1,
  "areaId": 1
}
```

A exposição devolvida na resposta é `probabilidade × impactoFinanceiro`.

## Executando os testes

Os testes de persistência acessam `cybhorrisco_test` e leem `DB_TEST_PASSWORD` do `.env` local. Os testes unitários e da camada web não dependem de uma porta HTTP aberta.

No Windows:

```powershell
.\mvnw.cmd test
```

No macOS ou Linux:

```bash
./mvnw test
```

## Organização das camadas

O projeto segue o recorte das aulas 03 a 07 do curso:

- `domain`: regras de negócio em Java, com JPA
- `repository`: Spring Data JPA
- `application`: serviços transacionais
- `api`: controllers REST, DTOs, mapeadores e tratamento de erros
- `db/changelog`: evolução do esquema com Liquibase
