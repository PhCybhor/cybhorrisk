package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.Interessado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InteressadoRepository extends JpaRepository<Interessado, Long>, JpaSpecificationExecutor<Interessado> {
    boolean existsByPessoaId(Long pessoaId);

    @EntityGraph(attributePaths = "pessoa")
    Optional<Interessado> findOneById(Long id);

    @Override
    @EntityGraph(attributePaths = "pessoa")
    Page<Interessado> findAll(Specification<Interessado> specification, Pageable pageable);
}
