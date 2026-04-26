package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasseportRepository extends JpaRepository<Passeport, Integer> {
    Optional<Passeport> findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId);
    Optional<Passeport> findByNumero(String numero);
}
