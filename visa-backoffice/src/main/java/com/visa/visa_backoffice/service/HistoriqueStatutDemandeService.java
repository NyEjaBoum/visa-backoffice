package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.HistoriqueStatutDemande;
import com.visa.visa_backoffice.model.Statut;
import com.visa.visa_backoffice.repository.HistoriqueStatutDemandeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HistoriqueStatutDemandeService {

    private final HistoriqueStatutDemandeRepository repository;

    public HistoriqueStatutDemandeService(HistoriqueStatutDemandeRepository repository) {
        this.repository = repository;
    }

    public HistoriqueStatutDemande findByIdOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HistoriqueStatutDemande introuvable : " + id));
    }

    public HistoriqueStatutDemande creer(Demande demande, Statut statut) {
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande(demande, statut);
        return repository.save(historique);
    }

    public HistoriqueStatutDemande update(Integer id, Demande demande, Statut statut) {
        HistoriqueStatutDemande historique = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HistoriqueStatutDemande introuvable : " + id));
        historique.setDemande(demande);
        historique.setStatut(statut);
        return repository.save(historique);
    }
}