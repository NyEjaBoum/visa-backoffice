package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.PieceFournie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PieceFournieRepository extends JpaRepository<PieceFournie, Integer> {

    @Query("SELECT pf FROM PieceFournie pf " +
           "JOIN FETCH pf.pieceJustificative pj " +
           "WHERE pf.demande.id = :demandeId")
    List<PieceFournie> findAllForDemande(@Param("demandeId") Integer demandeId);

    @Query("SELECT pf.pieceJustificative.id FROM PieceFournie pf " +
           "WHERE pf.demande.id = :demandeId AND pf.isPresent = true")
    List<Integer> findPresentPieceIds(@Param("demandeId") Integer demandeId);

    void deleteAllByDemande_Id(Integer demandeId);
}
