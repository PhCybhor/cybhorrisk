package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RiscoResponse(
        Long id,
        String codigo,
        String descricao,
        BigDecimal probabilidade,
        BigDecimal impactoFinanceiro,
        BigDecimal limiteTolerancia,
        BigDecimal exposicao,
        LocalDate dataIdentificacao,
        Status status,
        Long categoriaId,
        String categoriaNome,
        Long areaId,
        String areaNome) {
}
