package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.PerfilAcesso;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record PerfisContaAcessoRequest(
        @NotEmpty(message = "Informe ao menos um perfil")
        Set<PerfilAcesso> perfis) {
}
