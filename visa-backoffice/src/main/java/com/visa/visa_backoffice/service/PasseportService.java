package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.repository.PasseportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PasseportService {

    private final PasseportRepository repo;

    public PasseportService(PasseportRepository repo) {
        this.repo = repo;
    }

    public List<Passeport> findAll() {
        return repo.findAll();
    }

    public Optional<Passeport> findById(Integer id) {
        return repo.findById(id);
    }

    public Passeport getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passeport introuvable"));
    }

    public Passeport create(Passeport passeport) {
        return repo.save(passeport);
    }

    public Passeport update(Integer id, Passeport passeport) {
        Passeport existing = getOrThrow(id);
        existing.setIndividu(passeport.getIndividu());
        existing.setNumeroPass(passeport.getNumeroPass());
        existing.setDateDelivrance(passeport.getDateDelivrance());
        existing.setDateExpiration(passeport.getDateExpiration());
        existing.setActive(passeport.getActive());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}
