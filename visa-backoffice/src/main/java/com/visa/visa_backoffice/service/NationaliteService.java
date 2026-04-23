package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Nationalite;
import com.visa.visa_backoffice.repository.NationaliteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NationaliteService {

    private final NationaliteRepository nationaliteRepository;

    public NationaliteService(NationaliteRepository nationaliteRepository) {
        this.nationaliteRepository = nationaliteRepository;
    }

    public List<Nationalite> findAll() {
        return nationaliteRepository.findAll();
    }

    public Nationalite findById(Integer id) {
        return nationaliteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nationalite introuvable : " + id));
    }

    @Transactional
    public Nationalite create(Nationalite nationalite) {
        return nationaliteRepository.save(nationalite);
    }

    @Transactional
    public Nationalite update(Integer id, Nationalite payload) {
        Nationalite existing = findById(id);
        existing.setLibelle(payload.getLibelle());
        return nationaliteRepository.save(existing);
    }
}
