package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.domain.Status;
import com.curso.cybhorrisco.repository.AreaResponsavelRepository;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class RiscoService {

    private final RiscoRepository riscoRepository;
    private final CategoriaRiscoRepository categoriaRepository;
    private final AreaResponsavelRepository areaRepository;
    private final EntityManager entityManager;

    public RiscoService(
            RiscoRepository riscoRepository,
            CategoriaRiscoRepository categoriaRepository,
            AreaResponsavelRepository areaRepository,
            EntityManager entityManager) {
        this.riscoRepository = riscoRepository;
        this.categoriaRepository = categoriaRepository;
        this.areaRepository = areaRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Risco cadastrar(Risco risco, Long categoriaId, Long areaId) {
        if (riscoRepository.existsByCodigo(risco.getCodigo())) {
            throw new RecursoDuplicadoException("Código já cadastrado");
        }

        CategoriaRisco categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de risco não encontrada"));
        categoria.adicionarRisco(risco);

        if (areaId != null) {
            AreaResponsavel area = areaRepository.findById(areaId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Área responsável não encontrada"));
            risco.associarArea(area);
        }

        return riscoRepository.save(risco);
    }

    @Transactional(readOnly = true)
    public Risco buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public List<Risco> listar() {
        return riscoRepository.buscarTodosComRelacionamentos();
    }

    @Transactional
    public Risco registrarOcorrencia(Long id, BigDecimal incremento) {
        Risco risco = buscarEntidade(id);
        entityManager.refresh(risco, LockModeType.PESSIMISTIC_WRITE);
        risco.registrarOcorrencia(incremento);
        return risco;
    }

    @Transactional
    public Risco mitigar(Long id, BigDecimal reducao) {
        Risco risco = buscarEntidade(id);
        entityManager.refresh(risco, LockModeType.PESSIMISTIC_WRITE);
        risco.mitigar(reducao);
        return risco;
    }

    @Transactional
    public Risco alterar(
            Long id,
            String descricao,
            BigDecimal impactoFinanceiro,
            BigDecimal limiteTolerancia,
            Long categoriaId,
            Long areaId) {
        Risco risco = buscarEntidade(id);
        CategoriaRisco categoria = buscarCategoria(categoriaId);
        AreaResponsavel area = areaId == null
                ? null
                : buscarArea(areaId);

        risco.alterarDescricao(descricao);
        risco.alterarImpactoFinanceiro(impactoFinanceiro);
        risco.alterarLimiteTolerancia(limiteTolerancia);
        risco.alterarCategoria(categoria);
        risco.alterarArea(area);
        return risco;
    }

    @Transactional
    public Risco alterarStatus(Long id, Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
        Risco risco = buscarEntidade(id);
        if (status == Status.ATIVO) {
            risco.ativar();
        } else {
            risco.inativar();
        }
        return risco;
    }

    @Transactional
    public void excluir(Long id) {
        Risco risco = buscarEntidade(id);
        riscoRepository.delete(risco);
        riscoRepository.flush();
    }

    @Transactional(readOnly = true)
    public Page<Risco> pesquisar(
            String descricao,
            Status status,
            Long categoriaId,
            Long areaId,
            Boolean acimaLimiteTolerancia,
            Pageable pageable) {
        validarOrdenacao(
                pageable,
                Set.of("id", "codigo", "descricao", "probabilidade",
                        "impactoFinanceiro", "limiteTolerancia",
                        "dataIdentificacao", "status"));
        Specification<Risco> filtros = (root, query, cb) -> cb.conjunction();

        if (descricao != null && !descricao.isBlank()) {
            String trecho = "%" + descricao.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("descricao")), trecho));
        }
        if (status != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }
        if (categoriaId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("categoria").get("id"), categoriaId));
        }
        if (areaId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("areaResponsavel").get("id"), areaId));
        }
        if (Boolean.TRUE.equals(acimaLimiteTolerancia)) {
            filtros = filtros.and((root, query, cb) ->
                    cb.gt(
                            cb.prod(root.get("probabilidade"), root.get("impactoFinanceiro")),
                            root.get("limiteTolerancia")));
        }

        return riscoRepository.findAll(filtros, pageable);
    }

    private Risco buscarEntidade(Long id) {
        return riscoRepository.buscarPorIdComRelacionamentos(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Risco não encontrado"));
    }

    private CategoriaRisco buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de risco não encontrada"));
    }

    private AreaResponsavel buscarArea(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Área responsável não encontrada"));
    }

    private void validarOrdenacao(Pageable pageable, Set<String> camposPermitidos) {
        pageable.getSort().forEach(ordem -> {
            if (!camposPermitidos.contains(ordem.getProperty())) {
                throw new IllegalArgumentException(
                        "Campo de ordenação inválido: " + ordem.getProperty());
            }
        });
    }
}
