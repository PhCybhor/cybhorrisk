package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.AnalistaResponse;
import com.curso.cybhorrisco.domain.Analista;
import com.curso.cybhorrisco.domain.Pessoa;
import org.springframework.stereotype.Component;

@Component
public class AnalistaMapper {
    public AnalistaResponse toResponse(Analista colaborador) {
        Pessoa pessoa = colaborador.getPessoa();
        return new AnalistaResponse(colaborador.getId(), pessoa.getId(), pessoa.getNome(), pessoa.getEmail(),
                pessoa.getCpf(), pessoa.getStatus(), colaborador.getMatricula(), colaborador.getFuncao(),
                colaborador.getDataAdmissao());
    }
}
