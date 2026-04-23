package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Individu;
import com.visa.visa_backoffice.repository.IndividuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class IndividuService {

    private final IndividuRepository individuRepository;

    public IndividuService(IndividuRepository individuRepository) {
        this.individuRepository = individuRepository;
    }

    @Transactional(readOnly = true)
    public List<Individu> findAll() {
        return individuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Individu findByIdOrThrow(Integer id) {
        return individuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individu introuvable : " + id));
    }

    public Individu create(Individu individu) {
        individu.setCreatedDate(LocalDateTime.now());
        individu.setUpdatedDate(LocalDateTime.now());
        return individuRepository.save(individu);
    }

    public Individu update(Integer id, Individu payload) {
        Individu existing = findByIdOrThrow(id);
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
        return individuRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<Individu> search(String q) {
        String query = q == null ? "" : q.trim();
        if (query.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query obligatoire");
        }
        return individuRepository.searchByNomOrPrenom(query);
    }
}
