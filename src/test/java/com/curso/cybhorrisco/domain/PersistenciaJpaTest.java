package com.curso.cybhorrisco.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class PersistenciaJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void devePersistirERelerCategoriaERisco() {
        CategoriaRisco categoria = new CategoriaRisco("Tecnológico");
        Risco risco = new Risco(
                "RSK-1001",
                "Indisponibilidade do sistema de autenticação",
                new BigDecimal("10.000"),
                new BigDecimal("89.90"),
                LocalDate.of(2026, 3, 10));

        categoria.adicionarRisco(risco);

        entityManager.persist(categoria);
        entityManager.persist(risco);
        entityManager.flush();

        Long riscoId = risco.getId();
        entityManager.clear();

        Risco riscoRecuperado = entityManager.find(Risco.class, riscoId);

        assertNotNull(riscoRecuperado);
        assertEquals("Indisponibilidade do sistema de autenticação", riscoRecuperado.getDescricao());
        assertEquals("Tecnológico", riscoRecuperado.getCategoria().getNome());
        assertEquals(Status.ATIVO, riscoRecuperado.getStatus());
    }

    @Test
    void deveRegistrarTodosOsChangeSetsDoCurso() {
        Integer quantidade = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM databasechangelog",
                Integer.class);

        assertEquals(17, quantidade);
    }

    @Test
    @Transactional
    void bancoDeveImpedirCodigoDuplicado() {
        Long categoriaId = inserirCategoriaDiretamente("Categoria para unicidade");

        inserirRiscoDiretamente(categoriaId, "CODIGO-REPETIDO", "Primeiro risco", "1.000", "10.00");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> inserirRiscoDiretamente(
                        categoriaId,
                        "CODIGO-REPETIDO",
                        "Segundo risco",
                        "1.000",
                        "20.00"));
    }

    @Test
    @Transactional
    void bancoDeveImpedirProbabilidadeNegativa() {
        Long categoriaId = inserirCategoriaDiretamente("Categoria para probabilidade");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> inserirRiscoDiretamente(
                        categoriaId,
                        "CODIGO-PROB-NEGATIVA",
                        "Risco inválido",
                        "-1.000",
                        "10.00"));
    }

    private Long inserirCategoriaDiretamente(String nome) {
        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO categoria_risco (nome, status)
                        VALUES (?, 'ATIVO')
                        RETURNING id
                        """,
                Long.class,
                nome);
    }

    private void inserirRiscoDiretamente(
            Long categoriaId,
            String codigo,
            String descricao,
            String probabilidade,
            String impacto) {
        jdbcTemplate.update(
                """
                        INSERT INTO risco (
                            codigo,
                            descricao,
                            probabilidade,
                            impacto_financeiro,
                            limite_tolerancia,
                            data_identificacao,
                            status,
                            categoria_risco_id
                        )
                        VALUES (?, ?, CAST(? AS NUMERIC), CAST(? AS NUMERIC), 0, DATE '2026-03-10', 'ATIVO', ?)
                        """,
                codigo,
                descricao,
                probabilidade,
                impacto,
                categoriaId);
    }
}
