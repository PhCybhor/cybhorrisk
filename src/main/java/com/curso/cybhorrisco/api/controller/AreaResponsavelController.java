package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.AreaResponsavelRequest;
import com.curso.cybhorrisco.api.dto.AreaResponsavelResponse;
import com.curso.cybhorrisco.api.exception.ApiError;
import com.curso.cybhorrisco.api.mapper.AreaResponsavelMapper;
import com.curso.cybhorrisco.application.AreaResponsavelService;
import com.curso.cybhorrisco.domain.AreaResponsavel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Áreas responsáveis", description = "Cadastro e consulta de áreas responsáveis")
@RestController
@RequestMapping("/api/areas-responsaveis")
public class AreaResponsavelController {

    private final AreaResponsavelService service;
    private final AreaResponsavelMapper mapper;

    public AreaResponsavelController(AreaResponsavelService service, AreaResponsavelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar área responsável")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Área cadastrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Sigla já utilizada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<AreaResponsavelResponse> cadastrar(
            @Valid @RequestBody AreaResponsavelRequest request) {
        AreaResponsavel area = service.cadastrar(mapper.toEntity(request));
        URI location = URI.create("/api/areas-responsaveis/" + area.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(area));
    }

    @Operation(summary = "Consultar área responsável por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Área encontrada"),
            @ApiResponse(responseCode = "404", description = "Área não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public AreaResponsavelResponse buscarPorId(
            @Parameter(description = "Identificador da área", example = "1")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Listar áreas responsáveis")
    @ApiResponse(responseCode = "200", description = "Áreas listadas")
    @GetMapping
    public List<AreaResponsavelResponse> listar() {
        return service.listar().stream().map(mapper::toResponse).toList();
    }
}
