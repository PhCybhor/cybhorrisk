package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.CategoriaRiscoResponse;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import org.springframework.stereotype.Component;

@Component
public class CategoriaRiscoMapper {

    public CategoriaRiscoResponse toResponse(CategoriaRisco categoria) {
        return new CategoriaRiscoResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getStatus());
    }
}
