package com.curso.cybhorrisco.api.controller;

import com.curso.cybhorrisco.api.dto.ItemMitigacaoRequest;
import com.curso.cybhorrisco.api.dto.PaginaResponse;
import com.curso.cybhorrisco.api.dto.PlanoMitigacaoRequest;
import com.curso.cybhorrisco.api.dto.PlanoMitigacaoResponse;
import com.curso.cybhorrisco.api.dto.PlanoMitigacaoResumoResponse;
import com.curso.cybhorrisco.api.exception.ApiError;
import com.curso.cybhorrisco.api.mapper.PlanoMitigacaoMapper;
import com.curso.cybhorrisco.application.PlanoMitigacaoService;
import com.curso.cybhorrisco.domain.PlanoMitigacao;
import com.curso.cybhorrisco.domain.StatusPlanoMitigacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;

@Tag(name = "Planos de Mitigação",
        description = "Plano, itens, custo histórico e aplicação transacional de mitigações")
@RestController
@RequestMapping("/api/planos-mitigacao")
public class PlanoMitigacaoController {

    private final PlanoMitigacaoService service;
    private final PlanoMitigacaoMapper mapper;

    public PlanoMitigacaoController(PlanoMitigacaoService service, PlanoMitigacaoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Abrir plano",
            description = "Cria um plano ABERTO para um interessado e um analista ativos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plano aberto"),
            @ApiResponse(responseCode = "400", description = "Participante inativo ou função incompatível",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Interessado ou analista não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<PlanoMitigacaoResponse> cadastrar(
            @Valid @RequestBody PlanoMitigacaoRequest request) {
        PlanoMitigacao plano = service.cadastrar(request.interessadoId(), request.analistaId());
        return ResponseEntity.created(URI.create("/api/planos-mitigacao/" + plano.getId()))
                .body(mapper.toResponse(plano));
    }

    @Operation(summary = "Consultar plano por ID",
            description = "Retorna participantes, itens, custos históricos e total.")
    @GetMapping("/{id}")
    public PlanoMitigacaoResponse buscar(@PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar planos de mitigação",
            description = "Filtra por status, participantes e intervalo inclusivo de datas.")
    @GetMapping
    public PaginaResponse<PlanoMitigacaoResumoResponse> pesquisar(
            @RequestParam(required = false) StatusPlanoMitigacao status,
            @RequestParam(required = false) Long interessadoId,
            @RequestParam(required = false) Long analistaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal,
            @PageableDefault(size = 20, sort = "dataPlano", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return PaginaResponse.de(
                service.pesquisar(status, interessadoId, analistaId, dataInicial, dataFinal, pageable),
                mapper::toResumo);
    }

    @Operation(summary = "Adicionar item",
            description = "Captura o custo estimado atual do risco; somente planos abertos aceitam itens.")
    @PostMapping("/{id}/itens")
    public PlanoMitigacaoResponse adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemMitigacaoRequest request) {
        return mapper.toResponse(service.adicionarItem(
                id, request.riscoId(), request.reducaoProbabilidade()));
    }

    @Operation(summary = "Remover item",
            description = "Remove um item enquanto o plano estiver aberto.")
    @DeleteMapping("/{planoId}/itens/{itemId}")
    public PlanoMitigacaoResponse removerItem(
            @PathVariable Long planoId,
            @PathVariable Long itemId) {
        return mapper.toResponse(service.removerItem(planoId, itemId));
    }

    @Operation(summary = "Finalizar plano",
            description = "Valida todo o plano, bloqueia os riscos em ordem determinística "
                    + "e aplica mitigações na mesma transação.")
    @PostMapping("/{id}/finalizacao")
    public PlanoMitigacaoResponse finalizar(@PathVariable Long id) {
        return mapper.toResponse(service.finalizar(id));
    }

    @Operation(summary = "Cancelar plano",
            description = "Cancela um plano ainda aberto; como não houve mitigação, "
                    + "a probabilidade não é alterada.")
    @PostMapping("/{id}/cancelamento")
    public PlanoMitigacaoResponse cancelar(@PathVariable Long id) {
        return mapper.toResponse(service.cancelar(id));
    }
}
