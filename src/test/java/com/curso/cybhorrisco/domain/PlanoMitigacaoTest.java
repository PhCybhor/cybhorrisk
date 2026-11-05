package com.curso.cybhorrisco.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlanoMitigacaoTest {

    @Test
    void deveCalcularTotalComCustoCapturadoNoMomentoDaInclusao() {
        PlanoMitigacao plano = novoPlano();
        Risco risco = novoRisco();
        plano.adicionarItem(risco, new BigDecimal("2.000"));

        risco.alterarImpactoFinanceiro(new BigDecimal("15.00"));

        assertEquals(new BigDecimal("20.00"), plano.calcularTotal());
        plano.finalizar();
        assertEquals(StatusPlanoMitigacao.FINALIZADA, plano.getStatus());
    }

    @Test
    void naoDeveAlterarPlanoFinalizado() {
        PlanoMitigacao plano = novoPlano();
        plano.adicionarItem(novoRisco(), BigDecimal.ONE);
        plano.finalizar();
        assertThrows(IllegalStateException.class, plano::cancelar);
    }

    private PlanoMitigacao novoPlano() {
        Pessoa pessoaInteressado = new Pessoa(
                "Interessado",
                "interessado@example.com",
                "52998224725",
                LocalDate.now());
        Pessoa pessoaAnalista = new Pessoa(
                "Analista",
                "analista@example.com",
                "11144477735",
                LocalDate.now());
        return new PlanoMitigacao(
                new Interessado(pessoaInteressado, null),
                new Analista(
                        pessoaAnalista,
                        "ANL-1",
                        FuncaoAnalista.ANALISTA,
                        LocalDate.now()),
                LocalDateTime.of(2026, 9, 29, 10, 0));
    }

    private Risco novoRisco() {
        return new Risco(
                "RSK-1",
                "Risco",
                new BigDecimal("10.000"),
                new BigDecimal("10.00"),
                LocalDate.now());
    }
}
