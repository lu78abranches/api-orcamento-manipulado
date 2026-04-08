package com.farmacia.api_orcamento_manipulado.repository;

import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


    // src/main/java/com/farmacia/api_orcamento_manipulado/repository/OrcamentoRepository.java


    @Repository
    public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
        // O JpaRepository já traz métodos como save(), findById(), delete(), etc.
    }


