package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Visa;
import com.visa.visa_backoffice.repository.VisaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class VisaService {

    private final VisaRepository repo;

    public VisaService(VisaRepository repo) {
        this.repo = repo;
    }

    public List<Visa> findAll() {
        return repo.findAll();
    }

    public Optional<Visa> findById(Integer id) {
        return repo.findById(id);
    }

    public Visa getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visa introuvable"));
    }

    public Optional<Visa> findLastByReferencePrefix(String prefix) {
        return repo.findTopByReferenceStartingWithOrderByReferenceDesc(prefix);
    }

    public Visa create(Visa visa) {
        return repo.save(visa);
    }

    public Visa update(Integer id, Visa visa) {
        Visa existing = getOrThrow(id);
        existing.setDemande(visa.getDemande());
        existing.setReference(visa.getReference());
        existing.setDateDebut(visa.getDateDebut());
        existing.setDateFin(visa.getDateFin());
        existing.setPasseport(visa.getPasseport());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}
