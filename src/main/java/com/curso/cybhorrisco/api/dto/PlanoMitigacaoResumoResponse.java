package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.StatusPlanoMitigacao;

import java.time.LocalDateTime;

public record PlanoMitigacaoResumoResponse(
        Long id,
        Long interessadoId,
        String interessadoNome,
        Long analistaId,
        String analistaNome,
        LocalDateTime dataPlano,
        StatusPlanoMitigacao status) {
}
