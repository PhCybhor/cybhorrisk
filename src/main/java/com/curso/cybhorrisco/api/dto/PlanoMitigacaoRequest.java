package com.curso.cybhorrisco.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PlanoMitigacaoRequest(
        @NotNull @Schema(example = "1") Long interessadoId,
        @NotNull @Schema(example = "1") Long analistaId) { }
