package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.DemandeVisa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandeVisaRepository extends JpaRepository<DemandeVisa, Integer> {
    Optional<DemandeVisa> findTopByNumDemandeStartingWithOrderByNumDemandeDesc(String prefix);
}
