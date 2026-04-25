package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Utilisateur;
import com.visa.visa_backoffice.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public Utilisateur findByIdentifiantOrThrow(String identifiant) {
        return utilisateurRepository.findByIdentifiant(identifiant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiant ou mot de passe incorrect"));
    }

    @Transactional(readOnly = true)
    public Utilisateur findByIdOrThrow(Integer id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur create(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur update(Integer id, Utilisateur payload) {
        Utilisateur existing = findByIdOrThrow(id);
        if (payload.getIdentifiant() != null) {
            existing.setIdentifiant(payload.getIdentifiant());
        }
        if (payload.getMdp() != null) {
            existing.setMdp(payload.getMdp());
        }
        if (payload.getRole() != null) {
            existing.setRole(payload.getRole());
        }
        return utilisateurRepository.save(existing);
    }
}
