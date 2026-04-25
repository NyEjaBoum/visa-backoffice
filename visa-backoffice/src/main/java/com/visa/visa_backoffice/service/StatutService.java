package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Statut;
import com.visa.visa_backoffice.repository.StatutRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StatutService {

    private final StatutRepository statutRepository;

    public StatutService(StatutRepository statutRepository) {
        this.statutRepository = statutRepository;
    }

    public List<Statut> findAll() {
        return statutRepository.findAll();
    }

    public Statut findById(Integer id) {
        return statutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statut introuvable : " + id));
    }

    public Statut findByLibelle(String libelle) {
        return statutRepository.findByLibelle(libelle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statut introuvable : " + libelle));
    }

    public Statut getStatutCree() {
        return findByLibelle("CREE");
    }

    public Statut getStatutVisaApprouve() {
        return findByLibelle("VISA APPROUVE");
    }

    @Transactional
    public Statut create(Statut statut) {
        if (statut.getLibelle() != null) {
            statut.setLibelle(statut.getLibelle().trim());
        }
        return statutRepository.save(statut);
    }

    @Transactional
    public Statut update(Integer id, Statut payload) {
        Statut existing = findById(id);
        if (payload.getLibelle() != null) {
            existing.setLibelle(payload.getLibelle().trim());
        }
        return statutRepository.save(existing);
    }
}
