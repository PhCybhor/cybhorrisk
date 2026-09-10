package com.curso.cybhorrisco.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AreaResponsavelRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve possuir no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "Sigla é obrigatória")
        @Size(min = 2, max = 10, message = "Sigla deve possuir entre 2 e 10 caracteres")
        @Pattern(
                regexp = "[A-Z0-9]{2,10}",
                message = "Sigla deve conter apenas letras maiúsculas e números")
        String sigla) {
}
