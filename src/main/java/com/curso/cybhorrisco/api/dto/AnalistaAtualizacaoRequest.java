package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.FuncaoAnalista;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnalistaAtualizacaoRequest(
        @NotBlank @Size(max = 30) @Schema(example = "VEN-001") String matricula,
        @NotNull @Schema(example = "GERENTE") FuncaoAnalista funcao) { }
