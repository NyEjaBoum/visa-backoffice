package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeurRepository extends JpaRepository<Demandeur, Integer> {

    @Query("SELECT d FROM Demandeur d WHERE LOWER(d.nom) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.prenoms) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Demandeur> searchByNomOrPrenom(@Param("search") String search);
}
