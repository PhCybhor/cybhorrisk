package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.Status;

public record AreaResponsavelResponse(
        Long id,
        String nome,
        String sigla,
        Status status) {
}
