package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.CarteResident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {

	Optional<CarteResident> findByNumero(String numero);

	Optional<CarteResident> findByDemandeId(Integer demandeId);

	Optional<CarteResident> findTopByNumeroStartingWithOrderByNumeroDesc(String prefix);

	boolean existsByDemande_Demandeur_Id(Integer demandeurId);
}
