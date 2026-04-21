package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.TypeDemande;
import com.visa.visa_backoffice.repository.TypeDemandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TypeDemandeService {

    private final TypeDemandeRepository repo;

    public TypeDemandeService(TypeDemandeRepository repo) {
        this.repo = repo;
    }

    public List<TypeDemande> findAll() {
        return repo.findAll();
    }

    public Optional<TypeDemande> findById(Integer id) {
        return repo.findById(id);
    }

    public TypeDemande getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de demande introuvable"));
    }

    public TypeDemande create(TypeDemande typeDemande) {
        return repo.save(typeDemande);
    }

    public TypeDemande update(Integer id, TypeDemande typeDemande) {
        // Nomenclature: pas de setters -> on ne supporte pas l'update ici
        getOrThrow(id);
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Type de demande non modifiable");
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}
