package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.PerfilAcesso;
import com.curso.cybhorrisco.domain.StatusContaAcesso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Conta autenticável; nunca contém senha ou hash")
public record ContaAcessoResponse(
        Long id,
        Long pessoaId,
        String nomePessoa,
        String login,
        StatusContaAcesso status,
        Set<PerfilAcesso> perfis) {
}
