package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.VisaTransformable;
import com.visa.visa_backoffice.repository.VisaTransformableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<VisaTransformable> findByNumero(String numero) {
        if (numero == null || numero.isBlank()) return Optional.empty();
        return visaTransformableRepository.findByNumero(numero.trim());
    }

    @Transactional(readOnly = true)
    public Optional<VisaTransformable> findLastForDemandeur(Integer demandeurId) {
        if (demandeurId == null) return Optional.empty();
        return visaTransformableRepository.findFirstByDemandeurIdOrderByIdDesc(demandeurId);
    }
}
