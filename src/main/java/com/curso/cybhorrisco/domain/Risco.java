package com.curso.cybhorrisco.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "risco",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_risco_codigo",
                columnNames = "codigo"))
public class Risco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal probabilidade;

    @Column(name = "impacto_financeiro", nullable = false, precision = 18, scale = 2)
    private BigDecimal impactoFinanceiro;

    @Column(name = "limite_tolerancia", nullable = false, precision = 18, scale = 2)
    private BigDecimal limiteTolerancia;

    @Column(name = "data_identificacao", nullable = false)
    private LocalDate dataIdentificacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "categoria_risco_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_risco_categoria_risco"))
    private CategoriaRisco categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "area_responsavel_id",
            foreignKey = @ForeignKey(name = "fk_risco_area_responsavel"))
    private AreaResponsavel areaResponsavel;

    protected Risco() {
    }

    public Risco(
            String codigo,
            String descricao,
            BigDecimal probabilidade,
            BigDecimal impactoFinanceiro,
            LocalDate dataIdentificacao) {
        this(
                codigo,
                descricao,
                probabilidade,
                impactoFinanceiro,
                BigDecimal.ZERO,
                dataIdentificacao);
    }

    public Risco(
            String codigo,
            String descricao,
            BigDecimal probabilidade,
            BigDecimal impactoFinanceiro,
            BigDecimal limiteTolerancia,
            LocalDate dataIdentificacao) {
        this.codigo = validarTextoObrigatorio(codigo, "Código é obrigatório");
        this.descricao = validarTextoObrigatorio(descricao, "Descrição é obrigatória");
        this.probabilidade = validarNaoNegativo(
                probabilidade,
                "Probabilidade não pode ser negativa");
        this.impactoFinanceiro = validarNaoNegativo(
                impactoFinanceiro,
                "Impacto financeiro não pode ser negativo");
        this.limiteTolerancia = validarNaoNegativo(
                limiteTolerancia,
                "Limite de tolerância não pode ser negativo");
        this.dataIdentificacao = Objects.requireNonNull(
                dataIdentificacao,
                "Data de identificação é obrigatória");
        this.status = Status.ATIVO;
    }

    /**
     * Exposição = probabilidade × impacto financeiro.
     * A probabilidade funciona como fator quantitativo, no mesmo papel do saldo no projeto de referência.
     */
    public BigDecimal calcularExposicao() {
        return probabilidade
                .multiply(impactoFinanceiro)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void registrarOcorrencia(BigDecimal incremento) {
        validarPositivo(incremento, "Incremento de probabilidade deve ser maior que zero");
        this.probabilidade = probabilidade.add(incremento);
    }

    public void mitigar(BigDecimal reducao) {
        validarPositivo(reducao, "Redução de probabilidade deve ser maior que zero");

        if (probabilidade.compareTo(reducao) < 0) {
            throw new IllegalArgumentException("Probabilidade insuficiente para mitigação");
        }

        this.probabilidade = probabilidade.subtract(reducao);
    }

    public void alterarDescricao(String novaDescricao) {
        this.descricao = validarTextoObrigatorio(
                novaDescricao,
                "Descrição é obrigatória");
    }

    public void alterarImpactoFinanceiro(BigDecimal novoImpacto) {
        this.impactoFinanceiro = validarNaoNegativo(
                novoImpacto,
                "Impacto financeiro não pode ser negativo");
    }

    public void alterarLimiteTolerancia(BigDecimal novoLimite) {
        this.limiteTolerancia = validarNaoNegativo(
                novoLimite,
                "Limite de tolerância não pode ser negativo");
    }

    public void alterarCategoria(CategoriaRisco novaCategoria) {
        Objects.requireNonNull(
                novaCategoria,
                "Categoria de risco é obrigatória");
        if (this.categoria == novaCategoria) {
            return;
        }

        novaCategoria.validarInclusao(this);
        if (this.categoria != null) {
            this.categoria.removerInternamente(this);
        }
        this.categoria = novaCategoria;
        novaCategoria.adicionarInternamente(this);
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    void associarA(CategoriaRisco categoria) {
        Objects.requireNonNull(categoria, "Categoria de risco é obrigatória");

        if (this.categoria != null && this.categoria != categoria) {
            throw new IllegalStateException("Risco já pertence a outra categoria");
        }

        this.categoria = categoria;
    }

    public void associarArea(AreaResponsavel areaResponsavel) {
        this.areaResponsavel = Objects.requireNonNull(
                areaResponsavel,
                "Área responsável é obrigatória");
    }

    public void alterarArea(AreaResponsavel areaResponsavel) {
        this.areaResponsavel = areaResponsavel;
    }

    public String getCodigo() {
        return codigo;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getProbabilidade() {
        return probabilidade;
    }

    public BigDecimal getImpactoFinanceiro() {
        return impactoFinanceiro;
    }

    public BigDecimal getLimiteTolerancia() {
        return limiteTolerancia;
    }

    public LocalDate getDataIdentificacao() {
        return dataIdentificacao;
    }

    public Status getStatus() {
        return status;
    }

    public CategoriaRisco getCategoria() {
        return categoria;
    }

    public AreaResponsavel getAreaResponsavel() {
        return areaResponsavel;
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }

    private static BigDecimal validarNaoNegativo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static void validarPositivo(BigDecimal valor, String mensagem) {
        Objects.requireNonNull(valor, mensagem);
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}
