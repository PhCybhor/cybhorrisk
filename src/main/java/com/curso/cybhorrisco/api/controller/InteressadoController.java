package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.*;
import com.curso.cybhorrisco.api.mapper.InteressadoMapper;
import com.curso.cybhorrisco.application.InteressadoService;
import com.curso.cybhorrisco.domain.Interessado;
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

@Tag(name = "Interessados", description = "Papel de interessado associado a uma pessoa")
@RestController @RequestMapping("/api/interessados")
public class InteressadoController {
    private final InteressadoService service; private final InteressadoMapper mapper;
    public InteressadoController(InteressadoService service, InteressadoMapper mapper) { this.service = service; this.mapper = mapper; }

    @Operation(summary = "Cadastrar interessado", description = "Associa o papel Interessado a uma pessoa ativa já cadastrada.")
    @PostMapping public ResponseEntity<InteressadoResponse> cadastrar(@Valid @RequestBody InteressadoRequest request) {
        Interessado interessado = service.cadastrar(request.pessoaId(), request.telefone());
        return ResponseEntity.created(URI.create("/api/interessados/" + interessado.getId())).body(mapper.toResponse(interessado));
    }
    @Operation(summary = "Consultar interessado por ID")
    @GetMapping("/{id}") public InteressadoResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @Operation(summary = "Pesquisar interessados")
    @GetMapping public PaginaResponse<InteressadoResponse> pesquisar(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf, @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return PaginaResponse.de(service.pesquisar(nome, cpf, status, pageable), mapper::toResponse);
    }
    @Operation(summary = "Alterar interessado", description = "Altera os dados próprios do papel. Nome, e-mail e CPF são alterados em /api/pessoas/{id}.")
    @PutMapping("/{id}") public InteressadoResponse alterar(@PathVariable Long id, @Valid @RequestBody InteressadoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.telefone()));
    }
}
