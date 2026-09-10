package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.RiscoRequest;
import com.curso.cybhorrisco.api.dto.RiscoResponse;
import com.curso.cybhorrisco.api.mapper.RiscoMapper;
import com.curso.cybhorrisco.application.RiscoService;
import com.curso.cybhorrisco.domain.Risco;
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
@RequestMapping("/api/riscos")
public class RiscoController {

    private final RiscoService service;
    private final RiscoMapper mapper;

    public RiscoController(RiscoService service, RiscoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<RiscoResponse> cadastrar(
            @Valid @RequestBody RiscoRequest request) {
        Risco risco = mapper.toEntity(request);
        Risco cadastrado = service.cadastrar(
                risco,
                request.categoriaId(),
                request.areaId());
        URI location = URI.create("/api/riscos/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public RiscoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<RiscoResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
