package com.curso.cybhorrisco.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "area_responsavel",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_area_responsavel_sigla",
                columnNames = "sigla"))
public class AreaResponsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 10)
    private String sigla;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    protected AreaResponsavel() {
    }

    public AreaResponsavel(String nome, String sigla) {
        this.nome = validarTextoObrigatorio(nome, "Nome da área é obrigatório");
        this.sigla = validarSigla(sigla);
        this.status = Status.ATIVO;
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSigla() {
        return sigla;
    }

    public Status getStatus() {
        return status;
    }

    private static String validarTextoObrigatorio(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }

    private static String validarSigla(String sigla) {
        String valor = validarTextoObrigatorio(sigla, "Sigla é obrigatória").toUpperCase();
        if (!valor.matches("[A-Z0-9]{2,10}")) {
            throw new IllegalArgumentException(
                    "Sigla deve possuir de 2 a 10 caracteres alfanuméricos");
        }
        return valor;
    }
}
