package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.Analista;
import com.curso.cybhorrisco.domain.FuncaoAnalista;
import com.curso.cybhorrisco.domain.Pessoa;
import com.curso.cybhorrisco.domain.Status;
import com.curso.cybhorrisco.repository.AnalistaRepository;
import com.curso.cybhorrisco.repository.PessoaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

@Service
public class AnalistaService {
    private final AnalistaRepository repository;
    private final PessoaRepository pessoaRepository;

    public AnalistaService(AnalistaRepository repository, PessoaRepository pessoaRepository) {
        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public Analista cadastrar(Long pessoaId, String matricula, FuncaoAnalista funcao, LocalDate dataAdmissao) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));
        if (pessoa.getStatus() != Status.ATIVO) throw new IllegalArgumentException("Pessoa deve estar ativa");
        if (repository.existsByPessoaId(pessoaId)) throw new RecursoDuplicadoException("Pessoa já possui cadastro de analista");
        if (repository.existsByMatricula(matricula)) throw new RecursoDuplicadoException("Matrícula já cadastrada");
        return repository.save(new Analista(pessoa, matricula, funcao, dataAdmissao));
    }

    @Transactional(readOnly = true)
    public Analista buscarPorId(Long id) { return buscarEntidade(id); }

    @Transactional
    public Analista alterar(Long id, String matricula, FuncaoAnalista funcao) {
        Analista analista = buscarEntidade(id);
        if (repository.existsByMatriculaAndIdNot(matricula, id)) throw new RecursoDuplicadoException("Matrícula já cadastrada");
        analista.alterarDados(matricula, funcao);
        return analista;
    }

    @Transactional(readOnly = true)
    public Page<Analista> pesquisar(String nome, FuncaoAnalista funcao, Status status, Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "matricula", "funcao", "dataAdmissao", "pessoa.nome", "pessoa.status"));
        Specification<Analista> filtros = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) {
            String trecho = "%" + nome.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) -> cb.like(cb.lower(root.get("pessoa").get("nome")), trecho));
        }
        if (funcao != null) filtros = filtros.and((root, query, cb) -> cb.equal(root.get("funcao"), funcao));
        if (status != null) filtros = filtros.and((root, query, cb) -> cb.equal(root.get("pessoa").get("status"), status));
        return repository.findAll(filtros, pageable);
    }

    private Analista buscarEntidade(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Analista não encontrado"));
    }

    private void validarOrdenacao(Pageable pageable, Set<String> permitidos) {
        pageable.getSort().forEach(o -> {
            if (!permitidos.contains(o.getProperty())) throw new IllegalArgumentException("Campo de ordenação inválido: " + o.getProperty());
        });
    }
}
