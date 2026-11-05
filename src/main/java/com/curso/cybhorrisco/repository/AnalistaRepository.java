package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.Analista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AnalistaRepository extends JpaRepository<Analista, Long>, JpaSpecificationExecutor<Analista> {
    boolean existsByPessoaId(Long pessoaId);
    boolean existsByMatricula(String matricula);
    boolean existsByMatriculaAndIdNot(String matricula, Long id);

    @EntityGraph(attributePaths = "pessoa")
    Optional<Analista> findOneById(Long id);

    @Override
    @EntityGraph(attributePaths = "pessoa")
    Page<Analista> findAll(Specification<Analista> specification, Pageable pageable);
}
