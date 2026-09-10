package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.CategoriaRisco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRiscoRepository extends JpaRepository<CategoriaRisco, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<CategoriaRisco> findByNomeIgnoreCase(String nome);
}
