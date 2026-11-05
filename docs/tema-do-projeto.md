# Tema do projeto Cybhor Risk

## Identificação

- Nome: `cybhor-risco`
- Tema: gerenciadora simplificada de riscos corporativos
- Objetivo: cadastrar riscos e organizá-los por categorias, com área responsável opcional, interessados, analistas e planos de mitigação autenticados

## Entidade de classificação

- Nome no singular: `CategoriaRisco`
- Nome no plural: categorias de risco
- Descrição: classificação utilizada para organizar riscos
- Exemplos: Tecnológico, Operacional e Conformidade
- Status: ativo ou inativo

## Entidade principal

- Nome no singular: `Risco`
- Nome no plural: riscos
- Código único: código do risco
- Descrição: nome ou descrição do evento de risco
- Medida quantitativa: probabilidade
- Valor monetário: impacto financeiro estimado
- Valor calculado: probabilidade multiplicada pelo impacto financeiro (exposição)
- Data relevante: data de identificação
- Status: ativo ou inativo

## Entidade complementar (Aula 06)

- Nome no singular: `AreaResponsavel`
- Nome no plural: áreas responsáveis
- Identificador único: sigla
- Relacionamento: um risco pode ter uma área responsável opcional

## Pessoas e papéis (Aula 10)

- `Pessoa`: identidade civil compartilhada (nome, e-mail, CPF)
- `Interessado`: papel composto sobre `Pessoa` (telefone opcional)
- `Analista`: papel composto sobre `Pessoa` (matrícula, `FuncaoAnalista`, data de admissão)
- Funções: `ANALISTA`, `OPERACIONAL`, `GERENTE`, `ADMINISTRATIVO`

## Plano de mitigação (Aulas 10–11)

- `PlanoMitigacao`: composição com interessado, analista, status (`ABERTA` / `FINALIZADA` / `CANCELADA`) e itens
- `ItemMitigacao`: risco + `reducaoProbabilidade` + `custoEstimado` histórico (capturado do impacto financeiro)
- Finalizar aplica `mitigar()` em cada risco com `PESSIMISTIC_WRITE`, bloqueando riscos em ordem de ID
- Cancelar não altera probabilidade

## Segurança (Aulas 12–17)

- `ContaAcesso` ligada a `Pessoa`, perfis `OPERADOR` / `ADMINISTRADOR`
- JWT + refresh token com rotação, auditoria de eventos e proteção contra falhas consecutivas
- JaCoCo: mínimo 0,70 linha / 0,40 branch

## Relacionamento

```text
CategoriaRisco 1 ─────── N Risco
AreaResponsavel  1 ─────── N Risco
Pessoa 1 ─────── 0..1 Interessado
Pessoa 1 ─────── 0..1 Analista
Pessoa 1 ─────── 0..1 ContaAcesso
Interessado 1 ─────── N PlanoMitigacao
Analista 1 ─────── N PlanoMitigacao
PlanoMitigacao 1 ─────── N ItemMitigacao N ─────── 1 Risco
```

## Correspondência com o projeto de referência

| Projeto de referência (`suporteos2026`) | Este projeto (`cybhor-risco`) |
|---|---|
| `GrupoProduto` | `CategoriaRisco` |
| `Produto` | `Risco` |
| `Fornecedor` | `AreaResponsavel` |
| Código de barras | Código do risco |
| Saldo em estoque / `receberEstoque` / `retirarEstoque` | Probabilidade / `registrarOcorrencia` / `mitigar` |
| Valor unitário | Impacto financeiro |
| Valor do estoque | Exposição |
| Estoque mínimo / abaixo do mínimo | Limite de tolerância / acima do limite |
| Data de cadastro | Data de identificação |
| CNPJ | Sigla da área (`DocumentoFiscal` mantido para CPF) |
| `Pessoa` | `Pessoa` |
| `Cliente` | `Interessado` |
| `Colaborador` / `FuncaoColaborador` | `Analista` / `FuncaoAnalista` |
| `Venda` / `ItemVenda` / `StatusVenda` | `PlanoMitigacao` / `ItemMitigacao` / `StatusPlanoMitigacao` |
| `quantidade` / `precoUnitario` | `reducaoProbabilidade` / `custoEstimado` |
| `/api/clientes` / `/api/colaboradores` / `/api/vendas` | `/api/interessados` / `/api/analistas` / `/api/planos-mitigacao` |
| `ContaAcesso`, JWT, refresh, auditoria | mesmos conceitos no pacote `cybhorrisco` |

## Exemplos

| Categoria | Código | Risco | Probabilidade | Impacto financeiro |
|---|---|---|---:|---:|
| Tecnológico | `RSK-0001` | Indisponibilidade do sistema | 20 | 15000,00 |
| Operacional | `RSK-0002` | Falha no processo de backup | 15 | 8000,00 |
| Conformidade | `RSK-0003` | Vazamento de dados pessoais | 10 | 50000,00 |
