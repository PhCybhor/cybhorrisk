package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.Status;

public record CategoriaRiscoResponse(
        Long id,
        String nome,
        Status status) {
}
