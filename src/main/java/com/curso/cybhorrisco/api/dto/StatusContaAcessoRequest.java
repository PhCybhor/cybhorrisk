package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.StatusContaAcesso;
import jakarta.validation.constraints.NotNull;

public record StatusContaAcessoRequest(
        @NotNull(message = "Status é obrigatório")
        StatusContaAcesso status) {
}
