package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.CarteResident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarteResidentRepository extends JpaRepository<CarteResident, Integer> {
    Optional<CarteResident> findTopByReferenceStartingWithOrderByReferenceDesc(String prefix);
}
