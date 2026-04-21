package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.TypeVisa;
import com.visa.visa_backoffice.repository.TypeVisaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TypeVisaService {
    private final TypeVisaRepository repo;
    public TypeVisaService(TypeVisaRepository repo) { this.repo = repo; }

    public List<TypeVisa> findAll() { return repo.findAll(); }
    public Optional<TypeVisa> findById(Integer id) { return repo.findById(id); }

    public TypeVisa getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de visa introuvable"));
    }

    public TypeVisa create(TypeVisa e) { return repo.save(e); }

    public TypeVisa update(Integer id, TypeVisa e) {
        // Nomenclature: pas de setters -> on ne supporte pas l'update ici
        getOrThrow(id);
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Type de visa non modifiable");
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}