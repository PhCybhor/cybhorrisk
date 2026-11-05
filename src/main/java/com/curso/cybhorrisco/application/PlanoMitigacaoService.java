package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.Analista;
import com.curso.cybhorrisco.domain.FuncaoAnalista;
import com.curso.cybhorrisco.domain.Interessado;
import com.curso.cybhorrisco.domain.ItemMitigacao;
import com.curso.cybhorrisco.domain.PlanoMitigacao;
import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.domain.Status;
import com.curso.cybhorrisco.domain.StatusPlanoMitigacao;
import com.curso.cybhorrisco.repository.AnalistaRepository;
import com.curso.cybhorrisco.repository.InteressadoRepository;
import com.curso.cybhorrisco.repository.PlanoMitigacaoRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class PlanoMitigacaoService {

    private final PlanoMitigacaoRepository repository;
    private final InteressadoRepository interessadoRepository;
    private final AnalistaRepository analistaRepository;
    private final RiscoRepository riscoRepository;
    private final EntityManager entityManager;

    public PlanoMitigacaoService(
            PlanoMitigacaoRepository repository,
            InteressadoRepository interessadoRepository,
            AnalistaRepository analistaRepository,
            RiscoRepository riscoRepository,
            EntityManager entityManager) {
        this.repository = repository;
        this.interessadoRepository = interessadoRepository;
        this.analistaRepository = analistaRepository;
        this.riscoRepository = riscoRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public PlanoMitigacao cadastrar(Long interessadoId, Long analistaId) {
        Interessado interessado = buscarInteressado(interessadoId);
        Analista analista = buscarAnalista(analistaId);
        validarParticipantes(interessado, analista);
        return repository.save(new PlanoMitigacao(interessado, analista, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public PlanoMitigacao buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional
    public PlanoMitigacao adicionarItem(Long planoId, Long riscoId, BigDecimal reducaoProbabilidade) {
        PlanoMitigacao plano = buscarEntidade(planoId);
        Risco risco = riscoRepository.buscarPorIdComRelacionamentos(riscoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Risco não encontrado"));
        if (risco.getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Risco deve estar ativo");
        }
        plano.adicionarItem(risco, reducaoProbabilidade);
        return plano;
    }

    @Transactional
    public PlanoMitigacao removerItem(Long planoId, Long itemId) {
        PlanoMitigacao plano = buscarEntidade(planoId);
        plano.removerItem(itemId);
        return plano;
    }

    @Transactional
    public PlanoMitigacao finalizar(Long id) {
        PlanoMitigacao plano = buscarEntidade(id);
        validarParticipantes(plano.getInteressado(), plano.getAnalista());

        List<ItemMitigacao> itensOrdenados = plano.getItens().stream()
                .sorted(Comparator.comparing(item -> item.getRisco().getId()))
                .toList();

        for (ItemMitigacao item : itensOrdenados) {
            Risco risco = item.getRisco();
            entityManager.refresh(risco, LockModeType.PESSIMISTIC_WRITE);

            if (risco.getStatus() != Status.ATIVO) {
                throw new IllegalArgumentException("Todos os riscos do plano devem estar ativos");
            }
            risco.mitigar(item.getReducaoProbabilidade());
        }
        plano.finalizar();
        return plano;
    }

    @Transactional
    public PlanoMitigacao cancelar(Long id) {
        PlanoMitigacao plano = buscarEntidade(id);
        plano.cancelar();
        return plano;
    }

    @Transactional(readOnly = true)
    public Page<PlanoMitigacao> pesquisar(
            StatusPlanoMitigacao status,
            Long interessadoId,
            Long analistaId,
            LocalDate dataInicial,
            LocalDate dataFinal,
            Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "dataPlano", "status"));
        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
        Specification<PlanoMitigacao> filtros = (root, query, cb) -> cb.conjunction();
        if (status != null) {
            filtros = filtros.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (interessadoId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("interessado").get("id"), interessadoId));
        }
        if (analistaId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("analista").get("id"), analistaId));
        }
        if (dataInicial != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dataPlano"), dataInicial.atStartOfDay()));
        }
        if (dataFinal != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.lessThan(root.get("dataPlano"), dataFinal.plusDays(1).atStartOfDay()));
        }
        return repository.findAll(filtros, pageable);
    }

    private void validarParticipantes(Interessado interessado, Analista analista) {
        if (interessado.getPessoa().getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Interessado deve estar ativo");
        }
        if (analista.getPessoa().getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Analista deve estar ativo");
        }
        if (analista.getFuncao() != FuncaoAnalista.ANALISTA
                && analista.getFuncao() != FuncaoAnalista.GERENTE) {
            throw new IllegalArgumentException(
                    "Analista não possui função habilitada para planos de mitigação");
        }
    }

    private Interessado buscarInteressado(Long id) {
        return interessadoRepository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interessado não encontrado"));
    }

    private Analista buscarAnalista(Long id) {
        return analistaRepository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Analista não encontrado"));
    }

    private PlanoMitigacao buscarEntidade(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Plano de mitigação não encontrado"));
    }

    private void validarOrdenacao(Pageable pageable, Set<String> permitidos) {
        pageable.getSort().forEach(ordem -> {
            if (!permitidos.contains(ordem.getProperty())) {
                throw new IllegalArgumentException(
                        "Campo de ordenação inválido: " + ordem.getProperty());
            }
        });
    }
}
