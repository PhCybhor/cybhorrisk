package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.ContaAcessoResponse;
import com.curso.cybhorrisco.domain.ContaAcesso;
import org.springframework.stereotype.Component;

@Component
public class ContaAcessoMapper {

    public ContaAcessoResponse toResponse(ContaAcesso conta) {
        return new ContaAcessoResponse(
                conta.getId(),
                conta.getPessoa().getId(),
                conta.getPessoa().getNome(),
                conta.getLogin(),
                conta.getStatus(),
                conta.getPerfis());
    }
}
