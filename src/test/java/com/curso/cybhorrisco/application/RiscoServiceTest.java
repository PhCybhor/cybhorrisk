package com.curso.cybhorrisco.application;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import com.curso.cybhorrisco.domain.CategoriaRisco;
import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.repository.AreaResponsavelRepository;
import com.curso.cybhorrisco.repository.CategoriaRiscoRepository;
import com.curso.cybhorrisco.repository.RiscoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RiscoServiceTest {

    @Autowired
    private RiscoService riscoService;

    @Autowired
    private CategoriaRiscoRepository categoriaRepository;

    @Autowired
    private AreaResponsavelRepository areaRepository;

    @Autowired
    private RiscoRepository riscoRepository;

    @Test
    void deveCadastrarRiscoComCategoriaEArea() {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Operacional de teste"));
        AreaResponsavel area = areaRepository.save(new AreaResponsavel(
                "Área de teste",
                "TESTE"));

        Risco cadastrado = riscoService.cadastrar(
                novoRisco("TESTE-SERVICE-001"),
                categoria.getId(),
                area.getId());

        assertNotNull(cadastrado.getId());
        assertEquals(categoria.getId(), cadastrado.getCategoria().getId());
        assertEquals(area.getId(), cadastrado.getAreaResponsavel().getId());
    }

    @Test
    void deveImpedirCodigoDuplicado() {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria duplicidade"));
        riscoService.cadastrar(novoRisco("TESTE-DUPLICADO"), categoria.getId(), null);

        assertThrows(
                RecursoDuplicadoException.class,
                () -> riscoService.cadastrar(
                        novoRisco("TESTE-DUPLICADO"),
                        categoria.getId(),
                        null));
    }

    @Test
    void deveInformarCategoriaInexistenteSemSalvarRisco() {
        Risco risco = novoRisco("TESTE-SEM-CATEGORIA");

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> riscoService.cadastrar(risco, Long.MAX_VALUE, null));
        assertEquals(false, riscoRepository.existsByCodigo("TESTE-SEM-CATEGORIA"));
    }

    @Test
    void deveAtualizarProbabilidadePorDirtyChecking() {
        CategoriaRisco categoria = categoriaRepository.save(new CategoriaRisco("Categoria dirty checking"));
        Risco risco = riscoService.cadastrar(
                novoRisco("TESTE-DIRTY-CHECKING"),
                categoria.getId(),
                null);

        riscoService.registrarOcorrencia(risco.getId(), new BigDecimal("5.000"));

        Risco atualizado = riscoRepository.findById(risco.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("15.000").compareTo(atualizado.getProbabilidade()));
    }

    private Risco novoRisco(String codigo) {
        return new Risco(
                codigo,
                "Risco de teste",
                new BigDecimal("10.000"),
                new BigDecimal("25.90"),
                new BigDecimal("2.000"),
                LocalDate.of(2026, 8, 27));
    }
}
