package com.curso.cybhorrisco.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaRiscoTest {

    @Test
    void deveAdicionarRiscoEManejarOsDoisLadosDaAssociacao() {
        CategoriaRisco categoria = new CategoriaRisco("Tecnológico");
        Risco risco = novoRisco("RSK-0001");

        categoria.adicionarRisco(risco);

        assertEquals(1, categoria.getRiscos().size());
        assertSame(risco, categoria.getRiscos().getFirst());
        assertSame(categoria, risco.getCategoria());
    }

    @Test
    void naoDeveAdicionarRiscoNulo() {
        CategoriaRisco categoria = new CategoriaRisco("Tecnológico");

        assertThrows(NullPointerException.class, () -> categoria.adicionarRisco(null));
    }

    @Test
    void naoDeveAdicionarDoisRiscosComOMesmoCodigo() {
        CategoriaRisco categoria = new CategoriaRisco("Tecnológico");
        categoria.adicionarRisco(novoRisco("RSK-0001"));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> categoria.adicionarRisco(novoRisco("RSK-0001")));

        assertEquals("Código já utilizado na categoria", excecao.getMessage());
    }

    @Test
    void naoDevePermitirQueRiscoPertençaADuasCategorias() {
        CategoriaRisco tecnologico = new CategoriaRisco("Tecnológico");
        CategoriaRisco operacional = new CategoriaRisco("Operacional");
        Risco risco = novoRisco("RSK-0001");
        tecnologico.adicionarRisco(risco);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> operacional.adicionarRisco(risco));

        assertEquals("Risco já pertence a outra categoria", excecao.getMessage());
    }

    @Test
    void naoDeveExporUmaListaInternaModificavel() {
        CategoriaRisco categoria = new CategoriaRisco("Tecnológico");
        Risco risco = novoRisco("RSK-0001");
        categoria.adicionarRisco(risco);

        assertThrows(
                UnsupportedOperationException.class,
                () -> categoria.getRiscos().add(novoRisco("RSK-0002")));
    }

    private Risco novoRisco(String codigo) {
        return new Risco(
                codigo,
                "Falha de autenticação",
                new BigDecimal("3.000"),
                new BigDecimal("12.90"),
                LocalDate.of(2026, 8, 20));
    }
}
