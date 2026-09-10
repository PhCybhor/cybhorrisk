package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.AreaResponsavelRequest;
import com.curso.cybhorrisco.api.dto.AreaResponsavelResponse;
import com.curso.cybhorrisco.api.mapper.AreaResponsavelMapper;
import com.curso.cybhorrisco.application.AreaResponsavelService;
import com.curso.cybhorrisco.domain.AreaResponsavel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/areas-responsaveis")
public class AreaResponsavelController {

    private final AreaResponsavelService service;
    private final AreaResponsavelMapper mapper;

    public AreaResponsavelController(AreaResponsavelService service, AreaResponsavelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AreaResponsavelResponse> cadastrar(
            @Valid @RequestBody AreaResponsavelRequest request) {
        AreaResponsavel area = service.cadastrar(mapper.toEntity(request));
        URI location = URI.create("/api/areas-responsaveis/" + area.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(area));
    }

    @GetMapping("/{id}")
    public AreaResponsavelResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<AreaResponsavelResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
