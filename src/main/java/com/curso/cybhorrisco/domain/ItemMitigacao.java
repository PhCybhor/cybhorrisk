package com.curso.cybhorrisco.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(
        name = "item_mitigacao",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_item_mitigacao_risco",
                columnNames = {"plano_mitigacao_id", "risco_id"}))
public class ItemMitigacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "plano_mitigacao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_mitigacao_plano"))
    private PlanoMitigacao plano;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "risco_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_mitigacao_risco"))
    private Risco risco;

    @Column(name = "reducao_probabilidade", nullable = false, precision = 18, scale = 3)
    private BigDecimal reducaoProbabilidade;

    @Column(name = "custo_estimado", nullable = false, precision = 18, scale = 2)
    private BigDecimal custoEstimado;

    protected ItemMitigacao() {
    }

    ItemMitigacao(
            PlanoMitigacao plano,
            Risco risco,
            BigDecimal reducaoProbabilidade,
            BigDecimal custoEstimado) {
        this.plano = Objects.requireNonNull(plano, "Plano de mitigação é obrigatório");
        this.risco = Objects.requireNonNull(risco, "Risco é obrigatório");
        this.reducaoProbabilidade = validarPositivo(
                reducaoProbabilidade,
                "Redução de probabilidade deve ser maior que zero");
        this.custoEstimado = validarNaoNegativo(
                custoEstimado,
                "Custo estimado não pode ser negativo");
    }

    public BigDecimal calcularSubtotal() {
        return reducaoProbabilidade
                .multiply(custoEstimado)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public PlanoMitigacao getPlano() {
        return plano;
    }

    public Risco getRisco() {
        return risco;
    }

    public BigDecimal getReducaoProbabilidade() {
        return reducaoProbabilidade;
    }

    public BigDecimal getCustoEstimado() {
        return custoEstimado;
    }

    private static BigDecimal validarPositivo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }
}
