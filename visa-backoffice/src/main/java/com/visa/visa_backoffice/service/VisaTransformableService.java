package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.VisaTransformable;
import com.visa.visa_backoffice.repository.VisaTransformableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
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

    @Transactional(readOnly = true)
    public VisaTransformable findByIdOrThrow(Integer id) {
        return visaTransformableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "VisaTransformable introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Optional<VisaTransformable> findByNumero(String numero) {
        if (numero == null || numero.isBlank()) return Optional.empty();
        return visaTransformableRepository.findByNumero(numero.trim());
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }

    public VisaTransformable update(Integer id, VisaTransformable payload) {
        VisaTransformable existing = findByIdOrThrow(id);
        existing.setNumero(payload.getNumero());
        existing.setDateEntree(payload.getDateEntree());
        existing.setLieuEntree(payload.getLieuEntree());
        existing.setDateFinVisa(payload.getDateFinVisa());
        return visaTransformableRepository.save(existing);
    }
}
