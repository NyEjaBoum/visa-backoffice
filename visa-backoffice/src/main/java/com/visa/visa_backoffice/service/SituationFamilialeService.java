package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.SituationFamiliale;
import com.visa.visa_backoffice.repository.SituationFamilialeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SituationFamilialeService {

    private final SituationFamilialeRepository repo;

    public SituationFamilialeService(SituationFamilialeRepository repo) {
        this.repo = repo;
    }

    public List<SituationFamiliale> findAll() {
        return repo.findAll();
    }

    public Optional<SituationFamiliale> findById(Integer id) {
        return repo.findById(id);
    }

    public SituationFamiliale getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Situation familiale introuvable"));
    }

    public SituationFamiliale create(SituationFamiliale situationFamiliale) {
        return repo.save(situationFamiliale);
    }

    public SituationFamiliale update(Integer id, SituationFamiliale situationFamiliale) {
        SituationFamiliale existing = getOrThrow(id);
        existing.setLibelle(situationFamiliale.getLibelle());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}
