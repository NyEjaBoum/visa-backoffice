package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.TypeVisa;
import com.visa.visa_backoffice.repository.TypeVisaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TypeVisaService {

    private final TypeVisaRepository typeVisaRepository;

    public TypeVisaService(TypeVisaRepository typeVisaRepository) {
        this.typeVisaRepository = typeVisaRepository;
    }

    public List<TypeVisa> findAll() {
        return typeVisaRepository.findAll();
    }

    public TypeVisa findById(Integer id) {
        return typeVisaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de visa introuvable : " + id));
    }

    @Transactional
    public TypeVisa create(TypeVisa typeVisa) {
        return typeVisaRepository.save(typeVisa);
    }

    @Transactional
    public TypeVisa update(Integer id, TypeVisa payload) {
        TypeVisa existing = findById(id);
        existing.setLibelle(payload.getLibelle());
        return typeVisaRepository.save(existing);
    }
}
