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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "analista",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_analista_pessoa", columnNames = "pessoa_id"),
                @UniqueConstraint(name = "uk_analista_matricula", columnNames = "matricula")
        })
public class Analista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pessoa_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_analista_pessoa"))
    private Pessoa pessoa;

    @Column(nullable = false, length = 30)
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FuncaoAnalista funcao;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    protected Analista() {
    }

    public Analista(
            Pessoa pessoa,
            String matricula,
            FuncaoAnalista funcao,
            LocalDate dataAdmissao) {
        this.pessoa = Objects.requireNonNull(pessoa, "Pessoa é obrigatória");
        this.matricula = validarTextoObrigatorio(matricula, "Matrícula é obrigatória");
        this.funcao = Objects.requireNonNull(funcao, "Função é obrigatória");
        this.dataAdmissao = Objects.requireNonNull(
                dataAdmissao,
                "Data de admissão é obrigatória");
    }

    public void alterarDados(String matricula, FuncaoAnalista funcao) {
        this.matricula = validarTextoObrigatorio(matricula, "Matrícula é obrigatória");
        this.funcao = Objects.requireNonNull(funcao, "Função é obrigatória");
    }

    public Long getId() {
        return id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public String getMatricula() {
        return matricula;
    }

    public FuncaoAnalista getFuncao() {
        return funcao;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }
}
