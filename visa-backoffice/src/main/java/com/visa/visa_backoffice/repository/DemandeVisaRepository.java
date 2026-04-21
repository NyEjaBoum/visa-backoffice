package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.DemandeVisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemandeVisaRepository extends JpaRepository<DemandeVisa, Integer> {
    Optional<DemandeVisa> findTopByNumDemandeStartingWithOrderByNumDemandeDesc(String prefix);

    @Query("""
            SELECT d FROM DemandeVisa d
            JOIN FETCH d.individu i
            LEFT JOIN FETCH d.passeport p
            LEFT JOIN FETCH d.typeVisa tv
            LEFT JOIN FETCH d.typeDemande td
            LEFT JOIN FETCH d.statut s
            LEFT JOIN FETCH d.visaTransformable vt
            WHERE d.id = :id
            """)
    Optional<DemandeVisa> findByIdWithRefs(@Param("id") Integer id);

    @Query("""
            SELECT d FROM DemandeVisa d
            JOIN FETCH d.individu i
            LEFT JOIN FETCH d.typeVisa tv
            LEFT JOIN FETCH d.typeDemande td
            LEFT JOIN FETCH d.statut s
            ORDER BY d.dateCreation DESC
            """)
    List<DemandeVisa> findAllWithRefs();
}
