package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.DemandeVisa;
import com.visa.visa_backoffice.repository.DemandeVisaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class DemandeVisaService {

    private final DemandeVisaRepository repo;

    public DemandeVisaService(DemandeVisaRepository repo) {
        this.repo = repo;
    }

    public List<DemandeVisa> findAll() {
        return repo.findAll();
    }

    public Optional<DemandeVisa> findById(Integer id) {
        return repo.findById(id);
    }

    public DemandeVisa getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dossier introuvable"));
    }

    public DemandeVisa getWithRefsOrThrow(Integer id) {
        return repo.findByIdWithRefs(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dossier introuvable"));
    }

    public List<DemandeVisa> findAllWithRefs() {
        return repo.findAllWithRefs();
    }

    public Optional<DemandeVisa> findLastByNumeroPrefix(String prefix) {
        return repo.findTopByNumDemandeStartingWithOrderByNumDemandeDesc(prefix);
    }

    public DemandeVisa create(DemandeVisa d) {
        return repo.save(d);
    }

    public DemandeVisa update(Integer id, DemandeVisa d) {
        DemandeVisa existing = getOrThrow(id);
        existing.setNumDemande(d.getNumDemande());
        existing.setIndividu(d.getIndividu());
        existing.setPasseport(d.getPasseport());
        existing.setTypeVisa(d.getTypeVisa());
        existing.setTypeDemande(d.getTypeDemande());
        existing.setStatut(d.getStatut());
        existing.setVisaTransformable(d.getVisaTransformable());
        existing.setDateCreation(d.getDateCreation());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}