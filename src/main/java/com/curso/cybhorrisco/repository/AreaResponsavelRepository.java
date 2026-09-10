package com.curso.cybhorrisco.repository;

import com.curso.cybhorrisco.domain.AreaResponsavel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaResponsavelRepository extends JpaRepository<AreaResponsavel, Long> {

    boolean existsBySigla(String sigla);
}
