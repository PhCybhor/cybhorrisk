package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.Risco;
import com.curso.cybhorrisco.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RiscoRepository extends
        JpaRepository<Risco, Long>,
        JpaSpecificationExecutor<Risco> {

    Optional<Risco> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Risco> findByCategoriaId(Long categoriaId);

    boolean existsByCategoriaId(Long categoriaId);

    List<Risco> findByStatus(Status status);

    @EntityGraph(attributePaths = {"categoria", "areaResponsavel"})
    @Query("select r from Risco r order by r.id")
    List<Risco> buscarTodosComRelacionamentos();

    @EntityGraph(attributePaths = {"categoria", "areaResponsavel"})
    @Query("select r from Risco r where r.id = :id")
    Optional<Risco> buscarPorIdComRelacionamentos(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = {"categoria", "areaResponsavel"})
    Page<Risco> findAll(Specification<Risco> specification, Pageable pageable);
}
