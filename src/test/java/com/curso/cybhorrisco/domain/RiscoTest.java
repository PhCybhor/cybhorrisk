package com.curso.cybhorrisco.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiscoTest {

    @Test
    void deveCriarRiscoAtivoComDadosValidos() {
        Risco risco = novoRisco("3.000", "12.90");

        assertEquals("RSK-0001", risco.getCodigo());
        assertEquals("Falha de autenticação", risco.getDescricao());
        assertEquals(Status.ATIVO, risco.getStatus());
        assertEquals(LocalDate.of(2026, 8, 20), risco.getDataIdentificacao());
    }

    @Test
    void deveCalcularExposicao() {
        Risco risco = novoRisco("3.000", "12.90");

        BigDecimal exposicao = risco.calcularExposicao();

        assertEquals(0, new BigDecimal("38.70").compareTo(exposicao));
    }

    @Test
    void deveRegistrarOcorrenciaEMitigar() {
        Risco risco = novoRisco("3.000", "12.90");

        risco.registrarOcorrencia(new BigDecimal("2.500"));
        risco.mitigar(new BigDecimal("1.000"));

        assertEquals(0, new BigDecimal("4.500").compareTo(risco.getProbabilidade()));
    }

    @Test
    void naoDeveMitigarAcimaDaProbabilidade() {
        Risco risco = novoRisco("3.000", "12.90");

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> risco.mitigar(new BigDecimal("3.001")));

        assertEquals("Probabilidade insuficiente para mitigação", excecao.getMessage());
    }

    @Test
    void naoDeveCriarRiscoComCodigoEmBranco() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Risco(
                        " ",
                        "Falha de autenticação",
                        BigDecimal.ZERO,
                        new BigDecimal("12.90"),
                        LocalDate.of(2026, 8, 20)));
    }

    @Test
    void naoDeveCriarRiscoComProbabilidadeNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> novoRisco("-0.001", "12.90"));
    }

    @Test
    void deveAlterarOStatusPorComportamentoExplicito() {
        Risco risco = novoRisco("3.000", "12.90");

        risco.inativar();
        assertEquals(Status.INATIVO, risco.getStatus());

        risco.ativar();
        assertEquals(Status.ATIVO, risco.getStatus());
    }

    private Risco novoRisco(String probabilidade, String impactoFinanceiro) {
        return new Risco(
                "RSK-0001",
                "Falha de autenticação",
                new BigDecimal(probabilidade),
                new BigDecimal(impactoFinanceiro),
                LocalDate.of(2026, 8, 20));
    }
}
