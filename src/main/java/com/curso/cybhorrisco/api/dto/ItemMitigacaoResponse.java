package com.curso.cybhorrisco.api.dto;

import java.math.BigDecimal;

public record ItemMitigacaoResponse(Long id, Long riscoId, String riscoDescricao,
                                BigDecimal reducaoProbabilidade, BigDecimal custoEstimado,
                                BigDecimal subtotal) { }
