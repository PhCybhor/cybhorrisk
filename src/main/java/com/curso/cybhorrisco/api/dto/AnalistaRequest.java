package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.FuncaoAnalista;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AnalistaRequest(
        @NotNull @Schema(example = "2") Long pessoaId,
        @NotBlank @Size(max = 30) @Schema(example = "VEN-001") String matricula,
        @NotNull @Schema(example = "ANALISTA") FuncaoAnalista funcao,
        @NotNull @Schema(example = "2026-09-29") LocalDate dataAdmissao) { }
