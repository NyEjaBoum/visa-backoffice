package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.HistoriqueStatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriqueStatutDemandeRepository extends JpaRepository<HistoriqueStatutDemande, Integer> {
    List<HistoriqueStatutDemande> findByDemandeIdOrderByDateChangementDesc(Integer demandeId);

    List<HistoriqueStatutDemande> findByDemandeIdOrderByDateChangementAsc(Integer demandeId);
}