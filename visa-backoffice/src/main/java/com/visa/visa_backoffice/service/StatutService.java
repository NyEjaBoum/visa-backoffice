package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Statut;
import com.visa.visa_backoffice.repository.StatutRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class StatutService {

    private final StatutRepository repo;

    public StatutService(StatutRepository repo) {
        this.repo = repo;
    }

    public List<Statut> findAll() {
        return repo.findAll();
    }

    public Optional<Statut> findById(Integer id) {
        return repo.findById(id);
    }

    public Statut getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statut introuvable"));
    }

    /**
     * Pour les statuts "obligatoires" (ex: CREE/VALIDE) : si non config, c'est une erreur serveur.
     */
    public Statut getRequired(Integer id, String errorMessage) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage));
    }

    public Statut getRequiredByLibelle(String libelle) {
        return repo.findByLibelleIgnoreCase(libelle)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Statut '" + libelle + "' non configure en base"));
    }

    public Statut create(Statut statut) {
        return repo.save(statut);
    }

    public Statut update(Integer id, Statut statut) {
        // Nomenclature: pas de setters -> on ne supporte pas l'update ici
        getOrThrow(id);
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Statut non modifiable");
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}
