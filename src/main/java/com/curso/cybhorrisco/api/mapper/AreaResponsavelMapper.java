package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.AreaResponsavelRequest;
import com.curso.cybhorrisco.api.dto.AreaResponsavelResponse;
import com.curso.cybhorrisco.domain.AreaResponsavel;
import org.springframework.stereotype.Component;

@Component
public class AreaResponsavelMapper {

    public AreaResponsavel toEntity(AreaResponsavelRequest request) {
        return new AreaResponsavel(request.nome(), request.sigla());
    }

    public AreaResponsavelResponse toResponse(AreaResponsavel area) {
        return new AreaResponsavelResponse(
                area.getId(),
                area.getNome(),
                area.getSigla(),
                area.getStatus());
    }
}
