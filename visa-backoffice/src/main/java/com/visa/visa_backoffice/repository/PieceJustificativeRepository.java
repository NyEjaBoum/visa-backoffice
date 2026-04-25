package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Integer> {

    List<PieceJustificative> findAllByOrderByLibelleAsc();

    @Query("SELECT pj FROM PieceJustificative pj " +
           "WHERE pj.typeVisa IS NULL OR pj.typeVisa.id = :typeVisaId " +
           "ORDER BY pj.libelle")
    List<PieceJustificative> findForTypeVisa(@Param("typeVisaId") Integer typeVisaId);
}
