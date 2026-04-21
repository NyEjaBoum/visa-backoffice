package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.VisaTransformable;
import com.visa.visa_backoffice.repository.VisaTransformableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class VisaTransformableService {
    private final VisaTransformableRepository repo;
    public VisaTransformableService(VisaTransformableRepository repo) { this.repo = repo; }

    public List<VisaTransformable> findAll() { return repo.findAll(); }
    public Optional<VisaTransformable> findById(Integer id) { return repo.findById(id); }

    public VisaTransformable getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visa transformable introuvable"));
    }

    public Optional<VisaTransformable> findByNumeroReference(String ref) {
        return repo.findByNumeroReference(ref);
    }

    public VisaTransformable create(VisaTransformable v) { return repo.save(v); }

    public VisaTransformable update(Integer id, VisaTransformable v) {
        VisaTransformable existing = getOrThrow(id);
        existing.setIndividu(v.getIndividu());
        existing.setPasseport(v.getPasseport());
        existing.setNumeroReference(v.getNumeroReference());
        existing.setDateEntree(v.getDateEntree());
        existing.setLieuEntree(v.getLieuEntree());
        existing.setDateFinVisa(v.getDateFinVisa());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}