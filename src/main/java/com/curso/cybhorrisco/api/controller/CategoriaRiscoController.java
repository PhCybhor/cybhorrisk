package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.CategoriaRiscoRequest;
import com.curso.cybhorrisco.api.dto.CategoriaRiscoResponse;
import com.curso.cybhorrisco.api.mapper.CategoriaRiscoMapper;
import com.curso.cybhorrisco.application.CategoriaRiscoService;
import com.curso.cybhorrisco.domain.CategoriaRisco;
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
@RequestMapping("/api/categorias-risco")
public class CategoriaRiscoController {

    private final CategoriaRiscoService service;
    private final CategoriaRiscoMapper mapper;

    public CategoriaRiscoController(CategoriaRiscoService service, CategoriaRiscoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CategoriaRiscoResponse> cadastrar(
            @Valid @RequestBody CategoriaRiscoRequest request) {
        CategoriaRisco categoria = service.cadastrar(request.nome());
        URI location = URI.create("/api/categorias-risco/" + categoria.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(categoria));
    }

    @GetMapping("/{id}")
    public CategoriaRiscoResponse buscarPorId(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @GetMapping
    public List<CategoriaRiscoResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
