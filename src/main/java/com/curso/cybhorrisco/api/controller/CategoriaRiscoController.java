package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.CategoriaRiscoAtualizacaoRequest;
import com.curso.cybhorrisco.api.dto.CategoriaRiscoRequest;
import com.curso.cybhorrisco.api.dto.CategoriaRiscoResponse;
import com.curso.cybhorrisco.api.dto.PaginaResponse;
import com.curso.cybhorrisco.api.dto.StatusRequest;
import com.curso.cybhorrisco.api.exception.ApiError;
import com.curso.cybhorrisco.api.mapper.CategoriaRiscoMapper;
import com.curso.cybhorrisco.application.CategoriaRiscoService;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Categorias de risco", description = "Classificação, consulta e situação dos riscos")
@RestController
@RequestMapping("/api/categorias-risco")
public class CategoriaRiscoController {

    private final CategoriaRiscoService service;
    private final CategoriaRiscoMapper mapper;

    public CategoriaRiscoController(CategoriaRiscoService service, CategoriaRiscoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Já existe categoria com o mesmo nome",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<CategoriaRiscoResponse> cadastrar(
            @Valid @RequestBody CategoriaRiscoRequest request) {
        CategoriaRisco categoria = service.cadastrar(request.nome());
        URI location = URI.create("/api/categorias-risco/" + categoria.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(categoria));
    }

    @Operation(summary = "Consultar categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public CategoriaRiscoResponse buscarPorId(
            @Parameter(description = "Identificador da categoria", example = "1")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar categorias")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pesquisa executada"),
            @ApiResponse(responseCode = "400", description = "Paginação ou ordenação inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PaginaResponse<CategoriaRiscoResponse> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(nome, status, pageable),
                mapper::toResponse);
    }

    @Operation(summary = "Alterar categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria alterada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Nome já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public CategoriaRiscoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRiscoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(id, request.nome()));
    }

    @Operation(summary = "Ativar ou inativar categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/status")
    public CategoriaRiscoResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @Operation(summary = "Excluir categoria",
            description = "Exclui fisicamente somente quando nenhum risco utiliza a categoria.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Categoria em uso por riscos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
