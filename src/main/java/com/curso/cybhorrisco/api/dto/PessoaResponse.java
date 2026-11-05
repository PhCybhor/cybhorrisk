package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record PessoaResponse(Long id, String nome, String email, String cpf,
                             LocalDate dataCadastro, Status status) { }
