package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Demandeur;
import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.repository.PasseportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PasseportService {

    private final PasseportRepository passeportRepository;
    private final DemandeurService demandeurService;

    public PasseportService(PasseportRepository passeportRepository, DemandeurService demandeurService) {
        this.passeportRepository = passeportRepository;
        this.demandeurService = demandeurService;
    }

    @Transactional(readOnly = true)
    public List<Passeport> findAll() {
        return passeportRepository.findAll();
    }

    public Optional<Passeport> findByNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            return Optional.empty();
        }
        return passeportRepository.findByNumero(numero.trim());
    }

    @Transactional(readOnly = true)
    public Passeport findByIdOrThrow(Integer id) {
        return passeportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passeport introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Passeport> findLastForDemandeur(Integer demandeurId) {
        if (demandeurId == null) {
            return Optional.empty();
        }
        return passeportRepository.findFirstByDemandeurIdOrderByIdDesc(demandeurId);
    }

    public Passeport create(Passeport passeport) {
        return passeportRepository.save(passeport);
    }

    public Passeport update(Integer id, Passeport payload) {
        Passeport existing = findByIdOrThrow(id);
        existing.setNumero(payload.getNumero());
        existing.setDateDelivrance(payload.getDateDelivrance());
        existing.setDateExpiration(payload.getDateExpiration());
        return passeportRepository.save(existing);
    }
}
