package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.InteressadoResponse;
import com.curso.cybhorrisco.domain.Interessado;
import com.curso.cybhorrisco.domain.Pessoa;
import org.springframework.stereotype.Component;

@Component
public class InteressadoMapper {
    public InteressadoResponse toResponse(Interessado interessado) {
        Pessoa pessoa = interessado.getPessoa();
        return new InteressadoResponse(interessado.getId(), pessoa.getId(), pessoa.getNome(), pessoa.getEmail(),
                pessoa.getCpf(), interessado.getTelefone(), pessoa.getStatus());
    }
}
