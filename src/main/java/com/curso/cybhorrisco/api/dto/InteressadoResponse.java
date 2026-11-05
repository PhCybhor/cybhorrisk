package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.Status;

public record InteressadoResponse(Long id, Long pessoaId, String nome, String email,
                              String cpf, String telefone, Status status) { }
