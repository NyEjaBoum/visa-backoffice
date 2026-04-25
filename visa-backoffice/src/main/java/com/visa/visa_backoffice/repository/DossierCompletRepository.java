package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.DossierComplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DossierCompletRepository extends JpaRepository<DossierComplet, Integer> {

    @Query("SELECT d FROM DossierComplet d WHERE " +
           "LOWER(d.passeportNumero) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.prenoms) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.demandeNumero) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.visaTransformableNumero) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<DossierComplet> searchDossierExistants(@Param("search") String search);
}
