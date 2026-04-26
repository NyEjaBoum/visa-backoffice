package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Demandeur;
import com.visa.visa_backoffice.repository.DemandeurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DemandeurService {

    private final DemandeurRepository demandeurRepository;

    public DemandeurService(DemandeurRepository demandeurRepository) {
        this.demandeurRepository = demandeurRepository;
    }

    @Transactional(readOnly = true)
    public List<Demandeur> findAll() {
        return demandeurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Demandeur findByIdOrThrow(Integer id) {
        return demandeurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demandeur introuvable : " + id));
    }

    public Demandeur create(Demandeur demandeur) {
        demandeur.setCreatedDate(LocalDateTime.now());
        demandeur.setUpdatedDate(LocalDateTime.now());
        return demandeurRepository.save(demandeur);
    }

    public Demandeur save(Demandeur demandeur) {
        demandeur.setUpdatedDate(LocalDateTime.now());
        return demandeurRepository.save(demandeur);
    }

    public Demandeur update(Integer id, Demandeur payload) {
        Demandeur existing = findByIdOrThrow(id);
        existing.setNom(payload.getNom());
        existing.setPrenoms(payload.getPrenoms());
        existing.setNomJeuneFille(payload.getNomJeuneFille());
        existing.setDateNaissance(payload.getDateNaissance());
        existing.setProfession(payload.getProfession());
        existing.setAdresseMada(payload.getAdresseMada());
        existing.setContactMada(payload.getContactMada());
        existing.setNationalite(payload.getNationalite());
        existing.setSituationFamiliale(payload.getSituationFamiliale());
        existing.setUpdatedDate(LocalDateTime.now());
        return demandeurRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<Demandeur> search(String q) {
        String query = q == null ? "" : q.trim();
        if (query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query obligatoire");
        }
        return demandeurRepository.searchByNomOrPrenom(query);
    }
}
