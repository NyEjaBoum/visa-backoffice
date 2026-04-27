package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemandeRepository extends JpaRepository<Demande, Integer> {

    @Query("""
            SELECT d FROM Demande d
            JOIN FETCH d.demandeur de
            LEFT JOIN FETCH d.typeVisa tv
            LEFT JOIN FETCH d.typeDemande td
            LEFT JOIN FETCH d.statut s
            LEFT JOIN FETCH d.visaTransformable vt
            WHERE d.id = :id
            """)
    Optional<Demande> findByIdWithRefs(@Param("id") Integer id);

    @Query("""
            SELECT d FROM Demande d
            JOIN FETCH d.demandeur de
            LEFT JOIN FETCH d.typeVisa tv
            LEFT JOIN FETCH d.typeDemande td
            LEFT JOIN FETCH d.statut s
            LEFT JOIN FETCH d.visaTransformable vt
            ORDER BY d.dateCreation DESC
            """)
    List<Demande> findAllWithRefs();

    Optional<Demande> findTopByNumDemandeStartingWithOrderByNumDemandeDesc(String prefix);

        Optional<Demande> findByQrToken(UUID qrToken);

        Optional<Demande> findByNumDemande(String numDemande);

        Optional<Demande> findTopByDemandeur_IdOrderByDateCreationDesc(Integer demandeurId);
}
