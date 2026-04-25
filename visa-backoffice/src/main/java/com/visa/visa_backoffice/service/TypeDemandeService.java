package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.TypeDemande;
import com.visa.visa_backoffice.repository.TypeDemandeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TypeDemandeService {

    private final TypeDemandeRepository typeDemandeRepository;

    public TypeDemandeService(TypeDemandeRepository typeDemandeRepository) {
        this.typeDemandeRepository = typeDemandeRepository;
    }

    public List<TypeDemande> findAll() {
        return typeDemandeRepository.findAll();
    }

    public TypeDemande findById(Integer id) {
        return typeDemandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de demande introuvable : " + id));
    }

    public TypeDemande findByLibelleOrThrow(String libelle) {
        return typeDemandeRepository.findByLibelle(libelle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de demande introuvable : " + libelle));
    }

    @Transactional
    public TypeDemande create(TypeDemande typeDemande) {
        return typeDemandeRepository.save(typeDemande);
    }

    @Transactional
    public TypeDemande update(Integer id, TypeDemande payload) {
        TypeDemande existing = findById(id);
        existing.setLibelle(payload.getLibelle());
        return typeDemandeRepository.save(existing);
    }
}
