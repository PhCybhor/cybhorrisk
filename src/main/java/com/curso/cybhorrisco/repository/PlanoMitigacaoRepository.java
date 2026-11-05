package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.PlanoMitigacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PlanoMitigacaoRepository extends JpaRepository<PlanoMitigacao, Long>, JpaSpecificationExecutor<PlanoMitigacao> {
    @EntityGraph(attributePaths = {"interessado.pessoa", "analista.pessoa", "itens.risco"})
    Optional<PlanoMitigacao> findOneById(Long id);

    @Override
    @EntityGraph(attributePaths = {"interessado.pessoa", "analista.pessoa"})
    Page<PlanoMitigacao> findAll(Specification<PlanoMitigacao> specification, Pageable pageable);
}
