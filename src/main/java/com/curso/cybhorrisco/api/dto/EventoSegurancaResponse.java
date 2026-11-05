package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.TipoEventoSeguranca;

import java.time.Instant;

public record EventoSegurancaResponse(
        Long id,
        TipoEventoSeguranca tipo,
        Instant ocorridoEm,
        Long atorContaId,
        Long alvoContaId,
        String enderecoOrigem) {
}
