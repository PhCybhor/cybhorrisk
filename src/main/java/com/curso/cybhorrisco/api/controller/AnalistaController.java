package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.*;
import com.curso.cybhorrisco.api.mapper.AnalistaMapper;
import com.curso.cybhorrisco.application.AnalistaService;
import com.curso.cybhorrisco.domain.Analista;
import com.curso.cybhorrisco.domain.FuncaoAnalista;
import com.curso.cybhorrisco.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Analistas", description = "Vínculo profissional e função de negócio")
@RestController @RequestMapping("/api/analistas")
public class AnalistaController {
    private final AnalistaService service; private final AnalistaMapper mapper;
    public AnalistaController(AnalistaService service, AnalistaMapper mapper) { this.service = service; this.mapper = mapper; }

    @Operation(summary = "Cadastrar analista", description = "Associa um vínculo profissional a uma pessoa ativa.")
    @PostMapping public ResponseEntity<AnalistaResponse> cadastrar(@Valid @RequestBody AnalistaRequest request) {
        Analista c = service.cadastrar(request.pessoaId(), request.matricula(), request.funcao(), request.dataAdmissao());
        return ResponseEntity.created(URI.create("/api/analistas/" + c.getId())).body(mapper.toResponse(c));
    }
    @Operation(summary = "Consultar analista por ID")
    @GetMapping("/{id}") public AnalistaResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @Operation(summary = "Pesquisar analistas")
    @GetMapping public PaginaResponse<AnalistaResponse> pesquisar(@RequestParam(required = false) String nome,
            @RequestParam(required = false) FuncaoAnalista funcao, @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return PaginaResponse.de(service.pesquisar(nome, funcao, status, pageable), mapper::toResponse);
    }
    @Operation(summary = "Alterar analista", description = "Altera matrícula e função de negócio; não concede permissões de acesso.")
    @PutMapping("/{id}") public AnalistaResponse alterar(@PathVariable Long id, @Valid @RequestBody AnalistaAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.matricula(), request.funcao()));
    }
}
