# Tema do projeto Cybhor Risk

## Identificação

- Nome: `cybhor-risco`
- Tema: gerenciadora simplificada de riscos corporativos
- Objetivo: cadastrar riscos e organizá-los por categorias, com área responsável opcional

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

## Relacionamento

- Uma categoria de risco pode classificar vários riscos.
- Cada risco pertence a uma categoria de risco.
- Uma área responsável pode acompanhar vários riscos.
- A área de um risco é opcional.

```text
CategoriaRisco 1 ─────── N Risco
AreaResponsavel  1 ─────── N Risco
```

## Correspondência com o projeto de referência

| Projeto de referência (`suporteos2026`) | Este projeto (`cybhor-risco`) |
|---|---|
| `GrupoProduto` | `CategoriaRisco` |
| `Produto` | `Risco` |
| `Fornecedor` | `AreaResponsavel` |
| Código de barras | Código do risco |
| Saldo em estoque | Probabilidade |
| Valor unitário | Impacto financeiro |
| Valor do estoque | Exposição |
| Estoque mínimo | Limite de tolerância |
| Data de cadastro | Data de identificação |
| CNPJ | Sigla da área |

## Exemplos

| Categoria | Código | Risco | Probabilidade | Impacto financeiro |
|---|---|---|---:|---:|
| Tecnológico | `RSK-0001` | Indisponibilidade do sistema | 20 | 15000,00 |
| Operacional | `RSK-0002` | Falha no processo de backup | 15 | 8000,00 |
| Conformidade | `RSK-0003` | Vazamento de dados pessoais | 10 | 50000,00 |
