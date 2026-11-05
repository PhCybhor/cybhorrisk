package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.AjusteProbabilidadeRequest;
import com.curso.cybhorrisco.api.dto.PaginaResponse;
import com.curso.cybhorrisco.api.dto.RiscoAtualizacaoRequest;
import com.curso.cybhorrisco.api.dto.RiscoRequest;
import com.curso.cybhorrisco.api.dto.RiscoResponse;
import com.curso.cybhorrisco.api.dto.StatusRequest;
import com.curso.cybhorrisco.api.exception.ApiError;
import com.curso.cybhorrisco.api.mapper.RiscoMapper;
import com.curso.cybhorrisco.application.RiscoService;
import com.curso.cybhorrisco.domain.Risco;
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

@Tag(name = "Riscos", description = "Cadastro, consulta e ajuste de probabilidade")
@RestController
@RequestMapping("/api/riscos")
public class RiscoController {

    private final RiscoService service;
    private final RiscoMapper mapper;

    public RiscoController(RiscoService service, RiscoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar risco",
            description = "Cria um risco ativo associado a uma categoria e, opcionalmente, a uma área.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Risco cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Categoria ou área não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Código já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Consultar risco por ID",
            description = "Retorna a representação completa usada na visualização e na tela de alteração.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risco encontrado"),
            @ApiResponse(responseCode = "404", description = "Risco não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public RiscoResponse buscarPorId(
            @Parameter(description = "Identificador do risco", example = "1")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar riscos",
            description = "Combina filtros opcionais e executa paginação e ordenação no banco de dados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pesquisa executada"),
            @ApiResponse(responseCode = "400", description = "Paginação ou ordenação inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PaginaResponse<RiscoResponse> pesquisar(
            @Parameter(description = "Trecho da descrição, sem diferenciar maiúsculas de minúsculas",
                    example = "autenticação")
            @RequestParam(required = false) String descricao,
            @Parameter(description = "Status exato do risco", example = "ATIVO")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Identificador da categoria", example = "1")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Identificador da área responsável", example = "1")
            @RequestParam(required = false) Long areaId,
            @Parameter(description = "Quando verdadeiro, retorna riscos com exposição acima do limite",
                    example = "true")
            @RequestParam(required = false) Boolean acimaLimiteTolerancia,
            @Parameter(description = "Parâmetros page, size e sort. A página começa em zero.")
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(
                        descricao,
                        status,
                        categoriaId,
                        areaId,
                        acimaLimiteTolerancia,
                        pageable),
                mapper::toResponse);
    }

    @Operation(summary = "Alterar risco",
            description = "Substitui descrição, impacto, limite, categoria e área. "
                    + "Código, probabilidade e status possuem fluxos próprios.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risco alterado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Risco, categoria ou área não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public RiscoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody RiscoAtualizacaoRequest request) {
        return mapper.toResponse(service.alterar(
                id,
                request.descricao(),
                request.impactoFinanceiro(),
                request.limiteTolerancia(),
                request.categoriaId(),
                request.areaId()));
    }

    @Operation(summary = "Ativar ou inativar risco")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Risco não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/status")
    public RiscoResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @Operation(summary = "Registrar ocorrência",
            description = "Soma um valor positivo à probabilidade atual. Repetir a chamada repete o efeito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ocorrência registrada"),
            @ApiResponse(responseCode = "400", description = "Valor inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Risco não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/ocorrencias")
    public RiscoResponse registrarOcorrencia(
            @PathVariable Long id,
            @Valid @RequestBody AjusteProbabilidadeRequest request) {
        return mapper.toResponse(service.registrarOcorrencia(id, request.valor()));
    }

    @Operation(summary = "Registrar mitigação",
            description = "Subtrai um valor positivo da probabilidade sem permitir resultado negativo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mitigação registrada"),
            @ApiResponse(responseCode = "400", description = "Valor inválido ou probabilidade insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Risco não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/mitigacoes")
    public RiscoResponse mitigar(
            @PathVariable Long id,
            @Valid @RequestBody AjusteProbabilidadeRequest request) {
        return mapper.toResponse(service.mitigar(id, request.valor()));
    }

    @Operation(summary = "Excluir risco")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Risco excluído"),
            @ApiResponse(responseCode = "404", description = "Risco não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
