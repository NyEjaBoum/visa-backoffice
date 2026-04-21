package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.PieceFournie;
import com.visa.visa_backoffice.repository.PieceFournieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PieceFournieService {

    private final PieceFournieRepository repo;

    public PieceFournieService(PieceFournieRepository repo) {
        this.repo = repo;
    }

    public List<PieceFournie> findAll() { return repo.findAll(); }

    public Optional<PieceFournie> findById(Integer id) { return repo.findById(id); }

    public List<PieceFournie> findByDemandeId(Integer demandeId) {
        return repo.findByDemandeIdOrderByIdAsc(demandeId);
    }

    public PieceFournie create(PieceFournie p) { return repo.save(p); }

    public PieceFournie update(Integer id, PieceFournie p) {
        PieceFournie existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Piece fournie introuvable"));
        existing.setDemande(p.getDemande());
        existing.setLibellePiece(p.getLibellePiece());
        existing.setPresent(p.getPresent());
        return repo.save(existing);
    }

    public void delete(Integer id) { repo.deleteById(id); }
}