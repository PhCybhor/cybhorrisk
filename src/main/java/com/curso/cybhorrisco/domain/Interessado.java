package com.curso.cybhorrisco.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(
        name = "interessado",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_interessado_pessoa",
                columnNames = "pessoa_id"))
public class Interessado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pessoa_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_interessado_pessoa"))
    private Pessoa pessoa;

    @Column(length = 30)
    private String telefone;

    protected Interessado() {
    }

    public Interessado(Pessoa pessoa, String telefone) {
        this.pessoa = Objects.requireNonNull(pessoa, "Pessoa é obrigatória");
        this.telefone = normalizarOpcional(telefone);
    }

    public void alterarDados(String telefone) {
        this.telefone = normalizarOpcional(telefone);
    }

    public Long getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public String getTelefone() {
        return telefone;
    }

    private static String normalizarOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
