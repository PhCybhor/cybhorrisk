package com.curso.cybhorrisco.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AreaResponsavelTest {

    @Test
    void deveCriarAreaAtiva() {
        AreaResponsavel area = new AreaResponsavel(
                "Tecnologia da Informação",
                "TI");

        assertEquals("Tecnologia da Informação", area.getNome());
        assertEquals("TI", area.getSigla());
        assertEquals(Status.ATIVO, area.getStatus());
    }

    @Test
    void deveNormalizarSiglaParaMaiusculas() {
        AreaResponsavel area = new AreaResponsavel("Financeiro", "fin");

        assertEquals("FIN", area.getSigla());
    }

    @Test
    void deveRejeitarSiglaComFormatoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaResponsavel("Área", "T"));
    }

    @Test
    void deveRejeitarNomeVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AreaResponsavel(" ", "TI"));
    }

    @Test
    void deveInativarArea() {
        AreaResponsavel area = new AreaResponsavel("Tecnologia da Informação", "TI");

        area.inativar();

        assertEquals(Status.INATIVO, area.getStatus());
    }
}
