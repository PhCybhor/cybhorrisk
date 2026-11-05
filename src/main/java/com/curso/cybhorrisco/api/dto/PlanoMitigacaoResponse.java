package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.StatusPlanoMitigacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PlanoMitigacaoResponse(
        Long id,
        Long interessadoId,
        String interessadoNome,
        Long analistaId,
        String analistaNome,
        LocalDateTime dataPlano,
        StatusPlanoMitigacao status,
        List<ItemMitigacaoResponse> itens,
        BigDecimal total) {
}
