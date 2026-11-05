package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.Interessado;
import com.curso.cybhorrisco.domain.Pessoa;
import com.curso.cybhorrisco.domain.Status;
import com.curso.cybhorrisco.repository.InteressadoRepository;
import com.curso.cybhorrisco.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class InteressadoService {
    private final InteressadoRepository repository;
    private final PessoaRepository pessoaRepository;

    public InteressadoService(InteressadoRepository repository, PessoaRepository pessoaRepository) {
        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public Interessado cadastrar(Long pessoaId, String telefone) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));
        if (pessoa.getStatus() != Status.ATIVO) throw new IllegalArgumentException("Pessoa deve estar ativa");
        if (repository.existsByPessoaId(pessoaId)) throw new RecursoDuplicadoException("Pessoa já possui cadastro de interessado");
        return repository.save(new Interessado(pessoa, telefone));
    }

    @Transactional(readOnly = true)
    public Interessado buscarPorId(Long id) { return buscarEntidade(id); }

    @Transactional
    public Interessado alterar(Long id, String telefone) {
        Interessado interessado = buscarEntidade(id);
        interessado.alterarDados(telefone);
        return interessado;
    }

    @Transactional(readOnly = true)
    public Page<Interessado> pesquisar(String nome, String cpf, Status status, Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "telefone", "pessoa.nome", "pessoa.cpf", "pessoa.status"));
        Specification<Interessado> filtros = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) {
            String trecho = "%" + nome.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) -> cb.like(cb.lower(root.get("pessoa").get("nome")), trecho));
        }
        if (cpf != null && !cpf.isBlank()) filtros = filtros.and((root, query, cb) -> cb.equal(root.get("pessoa").get("cpf"), cpf.trim()));
        if (status != null) filtros = filtros.and((root, query, cb) -> cb.equal(root.get("pessoa").get("status"), status));
        return repository.findAll(filtros, pageable);
    }

    private Interessado buscarEntidade(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interessado não encontrado"));
    }

    private void validarOrdenacao(Pageable pageable, Set<String> permitidos) {
        pageable.getSort().forEach(o -> {
            if (!permitidos.contains(o.getProperty())) throw new IllegalArgumentException("Campo de ordenação inválido: " + o.getProperty());
        });
    }
}
