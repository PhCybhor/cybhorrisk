package com.curso.cybhorrisco.application;

import java.time.Instant;

public record TokensAutenticacao(
        TokenAcesso acesso,
        String refreshToken,
        Instant refreshTokenExpiraEm) {
}
