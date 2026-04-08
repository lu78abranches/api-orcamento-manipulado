package com.farmacia.api_orcamento_manipulado.repository;

import com.farmacia.api_orcamento_manipulado.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


    // src/main/java/com/farmacia/api_orcamento_manipulado/repository/OrcamentoRepository.java
    //Repository Pattern: separa a lógica de negócio (Java) da lógica de acesso a dados (SQL).
    // Você lida com objetos e o repositório cuida da persistência, permitindo trocar o banco de dados.



    @Repository
    public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
        // O JpaRepository já traz métodos como save(), findById(), delete(), etc.
    }


