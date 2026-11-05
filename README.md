# Cybhor Risk

API didática de **gerenciadora de risco**, desenvolvida no padrão da disciplina de Programação (Spring Boot 2026), a partir do projeto de referência [suporteos2026](https://github.com/jeffersonarpasserini/suporteos2026).

## Domínio

- Categoria de risco: classificação dos eventos (tecnológico, operacional, conformidade).
- Risco: evento identificado por código único, com probabilidade e impacto financeiro.
- Área responsável: setor opcional que acompanha o risco.
- Pessoa / Interessado / Analista: identidade civil e papéis compostos (não herança).
- Plano de mitigação: itens com redução de probabilidade e custo estimado histórico.
- Conta de acesso: autenticação JWT, refresh, auditoria e proteção de login.

## Requisitos

- Java 21
- Git
- IntelliJ IDEA (Ultimate é o caminho principal do curso; o Spring Initializr também funciona)
- Docker Desktop, utilizado nas aulas de PostgreSQL e containerização

## Correspondência com o curso

O `suporteos2026` demonstra controle de produtos por grupos. Este projeto mantém a mesma estrutura conceitual, trocando o tema:

```text
GrupoProduto   -> CategoriaRisco
Produto        -> Risco
Fornecedor     -> AreaResponsavel
Cliente        -> Interessado
Colaborador    -> Analista
Venda          -> PlanoMitigacao
ItemVenda      -> ItemMitigacao
ContaAcesso    -> ContaAcesso (mesmo conceito)
```

- [Consultar o tema do projeto](docs/tema-do-projeto.md)

## Executando o projeto

Na primeira execução, copie `.env.example` para `.env`, preencha `DB_DEV_PASSWORD`, `DB_TEST_PASSWORD` e `JWT_SECRET`, e mantenha esse arquivo fora do Git.

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

Com a aplicação iniciada, acesse <http://localhost:8080/api/health>. A resposta esperada é `OK`. Rotas de negócio exigem Bearer JWT (exceto health, OpenAPI e login/refresh/logout).

## Modelo de domínio

```text
CategoriaRisco 1 ─────── N Risco
AreaResponsavel  1 ─────── N Risco
Pessoa 1 ── 0..1 Interessado | Analista | ContaAcesso
Interessado / Analista ── N PlanoMitigacao ── N ItemMitigacao ── 1 Risco
```

A área responsável de um risco é opcional. As classes estão no pacote `com.curso.cybhorrisco.domain`, são mapeadas com JPA e preservam as regras de negócio. O Liquibase cria e versiona o esquema; o Hibernate apenas o valida. Os profiles `dev`, `test` e `prod` usam PostgreSQL, sem H2.

## API

Após iniciar a aplicação, estão disponíveis:

- `http://localhost:8080/api/categorias-risco`
- `http://localhost:8080/api/areas-responsaveis`
- `http://localhost:8080/api/riscos`
- `http://localhost:8080/api/pessoas`
- `http://localhost:8080/api/interessados`
- `http://localhost:8080/api/analistas`
- `http://localhost:8080/api/planos-mitigacao`
- `http://localhost:8080/api/contas-acesso`
- `http://localhost:8080/api/autenticacao`
- `http://localhost:8080/api/auditoria-seguranca`

Os contratos usam DTOs, validação de entrada, Bearer JWT no OpenAPI e respostas de erro padronizadas.

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

A exposição devolvida na resposta é `probabilidade × impactoFinanceiro`. Finalizar um plano de mitigação aplica `mitigar()` em cada item na mesma transação.

## Executando os testes

Os testes de persistência acessam `cybhorrisco_test` e leem `DB_TEST_PASSWORD` do `.env` local. Os testes unitários e da camada web não dependem de uma porta HTTP aberta.

```bash
set -a; source .env; set +a
./mvnw test
./mvnw verify   # inclui checagem JaCoCo (0.70 linha / 0.40 branch)
```

## Organização das camadas

O projeto segue o recorte das aulas 03 a 17 do curso:

- `domain`: regras de negócio em Java, com JPA
- `repository`: Spring Data JPA
- `application`: serviços transacionais (incluindo autenticação e auditoria)
- `api`: controllers REST, DTOs, mapeadores e tratamento de erros
- `config` / `security`: JWT, SecurityFilterChain e bootstrap do admin
- `db/changelog`: evolução do esquema com Liquibase (changesets 001–008)
