package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.CarteResident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {

	Optional<CarteResident> findTopByNumeroStartingWithOrderByNumeroDesc(String prefix);
}
