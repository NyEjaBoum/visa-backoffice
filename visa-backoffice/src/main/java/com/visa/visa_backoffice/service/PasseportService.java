package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Individu;
import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.repository.PasseportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class PasseportService {

    private final PasseportRepository passeportRepository;
    private final IndividuService individuService;

    public PasseportService(PasseportRepository passeportRepository, IndividuService individuService) {
        this.passeportRepository = passeportRepository;
        this.individuService = individuService;
    }

    @Transactional(readOnly = true)
    public List<Passeport> findAll() {
        return passeportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Passeport findByIdOrThrow(Integer id) {
        return passeportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passeport introuvable : " + id));
    }

    public Passeport create(Integer individuId, Passeport passeport) {
        Individu individu = individuService.findByIdOrThrow(individuId);
        passeport.setIndividu(individu);
        if (passeport.getActive() == null) {
            passeport.setActive(true);
        }
        return passeportRepository.save(passeport);
    }

    public Passeport update(Integer id, Passeport payload) {
        Passeport existing = findByIdOrThrow(id);
        existing.setNumeroPass(payload.getNumeroPass());
        existing.setDateDelivrance(payload.getDateDelivrance());
        existing.setDateExpiration(payload.getDateExpiration());
        if (payload.getActive() != null) {
            existing.setActive(payload.getActive());
        }
        return passeportRepository.save(existing);
    }
}
