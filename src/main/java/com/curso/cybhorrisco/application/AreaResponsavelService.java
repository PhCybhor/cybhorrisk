package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.repository.AreaResponsavelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaResponsavelService {

    private final AreaResponsavelRepository repository;

    public AreaResponsavelService(AreaResponsavelRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AreaResponsavel cadastrar(AreaResponsavel areaResponsavel) {
        if (repository.existsBySigla(areaResponsavel.getSigla())) {
            throw new RecursoDuplicadoException("Sigla já cadastrada");
        }
        return repository.save(areaResponsavel);
    }

    @Transactional(readOnly = true)
    public AreaResponsavel buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Área responsável não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<AreaResponsavel> listar() {
        return repository.findAll();
    }
}
