package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.Status;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CategoriaRiscoService {

    private final CategoriaRiscoRepository repository;
    private final RiscoRepository riscoRepository;

    public CategoriaRiscoService(
            CategoriaRiscoRepository repository,
            RiscoRepository riscoRepository) {
        this.repository = repository;
        this.riscoRepository = riscoRepository;
    }

    @Transactional
    public CategoriaRisco cadastrar(String nome) {
        String nomeNormalizado = normalizarNome(nome);
        if (repository.existsByNomeIgnoreCase(nomeNormalizado)) {
            throw new RecursoDuplicadoException("Nome da categoria já cadastrado");
        }
        return repository.save(new CategoriaRisco(nomeNormalizado));
    }

    @Transactional
    public CategoriaRisco alterar(Long id, String nome) {
        String nomeNormalizado = normalizarNome(nome);
        if (repository.existsByNomeIgnoreCaseAndIdNot(nomeNormalizado, id)) {
            throw new RecursoDuplicadoException("Nome da categoria já cadastrado");
        }

        CategoriaRisco categoria = buscarEntidade(id);
        categoria.alterarNome(nomeNormalizado);
        return categoria;
    }

    @Transactional
    public CategoriaRisco alterarStatus(Long id, Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
        CategoriaRisco categoria = buscarEntidade(id);
        if (status == Status.ATIVO) {
            categoria.ativar();
        } else {
            categoria.inativar();
        }
        return categoria;
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaRisco categoria = buscarEntidade(id);
        if (riscoRepository.existsByCategoriaId(id)) {
            throw new RecursoEmUsoException(
                    "Categoria de risco não pode ser excluída porque possui riscos");
        }
        repository.delete(categoria);
        repository.flush();
    }

    @Transactional(readOnly = true)
    public CategoriaRisco buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public List<CategoriaRisco> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<CategoriaRisco> pesquisar(
            String nome,
            Status status,
            Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "nome", "status"));
        Specification<CategoriaRisco> filtros = (root, query, cb) -> cb.conjunction();

        if (nome != null && !nome.isBlank()) {
            String trecho = "%" + nome.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), trecho));
        }
        if (status != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        return repository.findAll(filtros, pageable);
    }

    private CategoriaRisco buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de risco não encontrada"));
    }

    private String normalizarNome(String nome) {
        return nome == null ? null : nome.trim();
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
