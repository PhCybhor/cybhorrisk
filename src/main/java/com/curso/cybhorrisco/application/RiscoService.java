package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.repository.AreaResponsavelRepository;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RiscoService {

    private final RiscoRepository riscoRepository;
    private final CategoriaRiscoRepository categoriaRepository;
    private final AreaResponsavelRepository areaRepository;

    public RiscoService(
            RiscoRepository riscoRepository,
            CategoriaRiscoRepository categoriaRepository,
            AreaResponsavelRepository areaRepository) {
        this.riscoRepository = riscoRepository;
        this.categoriaRepository = categoriaRepository;
        this.areaRepository = areaRepository;
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
        return riscoRepository.buscarPorIdComRelacionamentos(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Risco não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Risco> listar() {
        return riscoRepository.buscarTodosComRelacionamentos();
    }

    @Transactional
    public Risco registrarOcorrencia(Long id, BigDecimal incremento) {
        Risco risco = riscoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Risco não encontrado"));
        risco.registrarOcorrencia(incremento);
        return risco;
    }
}
