package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.VisaTransformable;
import com.visa.visa_backoffice.repository.VisaTransformableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Optional;

@Service
@Transactional
public class VisaTransformableService {

    private final VisaTransformableRepository visaTransformableRepository;

    public VisaTransformableService(VisaTransformableRepository visaTransformableRepository) {
        this.visaTransformableRepository = visaTransformableRepository;
    }

    public VisaTransformable save(VisaTransformable vt) {
        return visaTransformableRepository.save(vt);
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

    public VisaTransformable update(Integer id, VisaTransformable payload) {
        VisaTransformable existing = findByIdOrThrow(id);
        existing.setNumero(payload.getNumero());
        existing.setDateEntree(payload.getDateEntree());
        existing.setLieuEntree(payload.getLieuEntree());
        existing.setDateFinVisa(payload.getDateFinVisa());
        return visaTransformableRepository.save(existing);
    }
}
