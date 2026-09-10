package com.curso.cybhorrisco.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RiscoRequest(
        @NotBlank(message = "Código é obrigatório")
        @Size(max = 50, message = "Código deve possuir no máximo 50 caracteres")
        String codigo,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 150, message = "Descrição deve possuir no máximo 150 caracteres")
        String descricao,

        @NotNull(message = "Probabilidade é obrigatória")
        @PositiveOrZero(message = "Probabilidade não pode ser negativa")
        BigDecimal probabilidade,

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
