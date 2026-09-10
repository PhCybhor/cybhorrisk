package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.RiscoRequest;
import com.curso.cybhorrisco.api.dto.RiscoResponse;
import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.domain.Risco;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RiscoMapper {

    public Risco toEntity(RiscoRequest request) {
        return new Risco(
                request.codigo(),
                request.descricao(),
                request.probabilidade(),
                request.impactoFinanceiro(),
                request.limiteTolerancia(),
                LocalDate.now());
    }

    public RiscoResponse toResponse(Risco risco) {
        AreaResponsavel area = risco.getAreaResponsavel();

        return new RiscoResponse(
                risco.getId(),
                risco.getCodigo(),
                risco.getDescricao(),
                risco.getProbabilidade(),
                risco.getImpactoFinanceiro(),
                risco.getLimiteTolerancia(),
                risco.calcularExposicao(),
                risco.getDataIdentificacao(),
                risco.getStatus(),
                risco.getCategoria().getId(),
                risco.getCategoria().getNome(),
                area == null ? null : area.getId(),
                area == null ? null : area.getNome());
    }
}
