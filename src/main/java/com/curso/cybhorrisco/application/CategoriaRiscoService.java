package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaRiscoService {

    private final CategoriaRiscoRepository repository;

    public CategoriaRiscoService(CategoriaRiscoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaRisco cadastrar(String nome) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new RecursoDuplicadoException("Nome da categoria já cadastrado");
        }
        return repository.save(new CategoriaRisco(nome));
    }

    @Transactional(readOnly = true)
    public CategoriaRisco buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de risco não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<CategoriaRisco> listar() {
        return repository.findAll();
    }
}
