package com.curso.cybhorrisco.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "plano_mitigacao")
public class PlanoMitigacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "interessado_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_plano_mitigacao_interessado"))
    private Interessado interessado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "analista_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_plano_mitigacao_analista"))
    private Analista analista;

    @Column(name = "data_plano", nullable = false)
    private LocalDateTime dataPlano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPlanoMitigacao status;

    @OneToMany(
            mappedBy = "plano",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ItemMitigacao> itens = new ArrayList<>();

    protected PlanoMitigacao() {
    }

    public PlanoMitigacao(Interessado interessado, Analista analista, LocalDateTime dataPlano) {
        this.interessado = Objects.requireNonNull(interessado, "Interessado é obrigatório");
        this.analista = Objects.requireNonNull(analista, "Analista é obrigatório");
        this.dataPlano = Objects.requireNonNull(dataPlano, "Data do plano é obrigatória");
        this.status = StatusPlanoMitigacao.ABERTA;
    }

    public ItemMitigacao adicionarItem(Risco risco, BigDecimal reducaoProbabilidade) {
        validarAberta();
        Objects.requireNonNull(risco, "Risco é obrigatório");
        boolean riscoRepetido = itens.stream()
                .anyMatch(item -> Objects.equals(
                        item.getRisco().getId(),
                        risco.getId()));
        if (riscoRepetido) {
            throw new IllegalArgumentException("Risco já incluído no plano de mitigação");
        }

        ItemMitigacao item = new ItemMitigacao(
                this,
                risco,
                reducaoProbabilidade,
                risco.getImpactoFinanceiro());
        itens.add(item);
        return item;
    }

    public void removerItem(Long itemId) {
        validarAberta();
        boolean removido = itens.removeIf(item -> Objects.equals(item.getId(), itemId));
        if (!removido) {
            throw new IllegalArgumentException("Item não pertence ao plano de mitigação");
        }
    }

    public void finalizar() {
        validarAberta();
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Plano deve possuir ao menos um item");
        }
        this.status = StatusPlanoMitigacao.FINALIZADA;
    }

    public void cancelar() {
        validarAberta();
        this.status = StatusPlanoMitigacao.CANCELADA;
    }

    public BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemMitigacao::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public Interessado getInteressado() {
        return interessado;
    }

    public Analista getAnalista() {
        return analista;
    }

    public LocalDateTime getDataPlano() {
        return dataPlano;
    }

    public StatusPlanoMitigacao getStatus() {
        return status;
    }

    public List<ItemMitigacao> getItens() {
        return List.copyOf(itens);
    }

    private void validarAberta() {
        if (status != StatusPlanoMitigacao.ABERTA) {
            throw new IllegalStateException("Somente planos abertos podem ser alterados");
        }
    }
}
