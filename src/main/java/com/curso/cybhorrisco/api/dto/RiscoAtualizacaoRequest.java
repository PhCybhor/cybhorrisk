package com.curso.cybhorrisco.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RiscoAtualizacaoRequest(
        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 150, message = "Descrição deve possuir no máximo 150 caracteres")
        String descricao,

        @NotNull(message = "Impacto financeiro é obrigatório")
        @PositiveOrZero(message = "Impacto financeiro não pode ser negativo")
        BigDecimal impactoFinanceiro,

        @NotNull(message = "Limite de tolerância é obrigatório")
        @PositiveOrZero(message = "Limite de tolerância não pode ser negativo")
        BigDecimal limiteTolerancia,

        @NotNull(message = "Categoria é obrigatória")
        @Positive(message = "Identificador da categoria deve ser positivo")
        Long categoriaId,

        @Positive(message = "Identificador da área responsável deve ser positivo")
        Long areaId) {
}
