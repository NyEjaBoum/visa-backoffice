package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Demandeur;
import com.visa.visa_backoffice.model.VisaTransformable;
import com.visa.visa_backoffice.repository.VisaTransformableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
public class VisaTransformableService {

    private final VisaTransformableRepository visaTransformableRepository;

    public VisaTransformableService(VisaTransformableRepository visaTransformableRepository) {
        this.visaTransformableRepository = visaTransformableRepository;
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

    @Transactional(readOnly = true)
    public Optional<VisaTransformable> findLastForDemandeur(Integer demandeurId) {
        if (demandeurId == null) return Optional.empty();
        return visaTransformableRepository.findFirstByDemandeurIdOrderByIdDesc(demandeurId);
    }

    public VisaTransformable findByDemandeur(Demandeur demandeur) {
        return findLastForDemandeur(demandeur.getId()).orElse(null);
    }

    /**
     * Sauvegarde un VT en vérifiant qu'il n'appartient pas à un autre demandeur.
     * Si le numéro existe déjà pour le même demandeur, l'enregistrement est réutilisé.
     */
    public VisaTransformable create(VisaTransformable vt, Demandeur owner) {
        if (vt.getId() == null) {
            VisaTransformable existing = findByNumero(vt.getNumero()).orElse(null);
            if (existing != null) {
                if (existing.getDemandeur() != null
                        && !existing.getDemandeur().getId().equals(owner.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Ce numéro de visa transformable appartient déjà à un autre usager.");
                }
                vt.setId(existing.getId());
            }
        }
        return visaTransformableRepository.save(vt);
    }

    public VisaTransformable save(VisaTransformable vt) {
        return visaTransformableRepository.save(vt);
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
