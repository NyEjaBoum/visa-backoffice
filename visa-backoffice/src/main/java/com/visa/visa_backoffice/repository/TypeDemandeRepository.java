package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Integer> {
    Optional<TypeDemande> findByLibelle(String libelle);
}
