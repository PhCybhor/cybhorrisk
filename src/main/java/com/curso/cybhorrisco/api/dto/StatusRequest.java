package com.curso.cybhorrisco.api.dto;

import com.curso.cybhorrisco.domain.Status;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "Status é obrigatório")
        Status status) {
}
