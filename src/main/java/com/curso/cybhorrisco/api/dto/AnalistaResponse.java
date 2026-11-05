package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.FuncaoAnalista;
import com.curso.cybhorrisco.domain.Status;

import java.time.LocalDate;

public record AnalistaResponse(Long id, Long pessoaId, String nome, String email,
                                  String cpf, Status status, String matricula,
                                  FuncaoAnalista funcao, LocalDate dataAdmissao) { }
