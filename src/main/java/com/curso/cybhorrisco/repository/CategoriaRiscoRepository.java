package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.CategoriaRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoriaRiscoRepository extends
        JpaRepository<CategoriaRisco, Long>,
        JpaSpecificationExecutor<CategoriaRisco> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    Optional<CategoriaRisco> findByNomeIgnoreCase(String nome);
}
