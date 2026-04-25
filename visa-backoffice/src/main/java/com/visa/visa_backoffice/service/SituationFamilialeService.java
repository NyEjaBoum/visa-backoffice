package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.SituationFamiliale;
import com.visa.visa_backoffice.repository.SituationFamilialeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SituationFamilialeService {

    private final SituationFamilialeRepository situationFamilialeRepository;

    public SituationFamilialeService(SituationFamilialeRepository situationFamilialeRepository) {
        this.situationFamilialeRepository = situationFamilialeRepository;
    }

    public List<SituationFamiliale> findAll() {
        return situationFamilialeRepository.findAll();
    }

    public SituationFamiliale findById(Integer id) {
        return situationFamilialeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Situation familiale introuvable : " + id));
    }

    @Transactional
    public SituationFamiliale create(SituationFamiliale situationFamiliale) {
        return situationFamilialeRepository.save(situationFamiliale);
    }

    @Transactional
    public SituationFamiliale update(Integer id, SituationFamiliale payload) {
        SituationFamiliale existing = findById(id);
        existing.setLibelle(payload.getLibelle());
        return situationFamilialeRepository.save(existing);
    }
}
