package com.curso.cybhorrisco.api.mapper;

import com.curso.cybhorrisco.api.dto.ItemMitigacaoResponse;
import com.curso.cybhorrisco.api.dto.PlanoMitigacaoResponse;
import com.curso.cybhorrisco.api.dto.PlanoMitigacaoResumoResponse;
import com.curso.cybhorrisco.domain.ItemMitigacao;
import com.curso.cybhorrisco.domain.PlanoMitigacao;
import org.springframework.stereotype.Component;

@Component
public class PlanoMitigacaoMapper {

    public PlanoMitigacaoResponse toResponse(PlanoMitigacao plano) {
        return new PlanoMitigacaoResponse(
                plano.getId(),
                plano.getInteressado().getId(),
                plano.getInteressado().getPessoa().getNome(),
                plano.getAnalista().getId(),
                plano.getAnalista().getPessoa().getNome(),
                plano.getDataPlano(),
                plano.getStatus(),
                plano.getItens().stream().map(this::toItemResponse).toList(),
                plano.calcularTotal());
    }

    public PlanoMitigacaoResumoResponse toResumo(PlanoMitigacao plano) {
        return new PlanoMitigacaoResumoResponse(
                plano.getId(),
                plano.getInteressado().getId(),
                plano.getInteressado().getPessoa().getNome(),
                plano.getAnalista().getId(),
                plano.getAnalista().getPessoa().getNome(),
                plano.getDataPlano(),
                plano.getStatus());
    }

    private ItemMitigacaoResponse toItemResponse(ItemMitigacao item) {
        return new ItemMitigacaoResponse(
                item.getId(),
                item.getRisco().getId(),
                item.getRisco().getDescricao(),
                item.getReducaoProbabilidade(),
                item.getCustoEstimado(),
                item.calcularSubtotal());
    }
}
