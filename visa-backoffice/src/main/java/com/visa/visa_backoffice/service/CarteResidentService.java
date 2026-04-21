package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.CarteResident;
import com.visa.visa_backoffice.repository.CarteResidentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CarteResidentService {

    private final CarteResidentRepository repo;

    public CarteResidentService(CarteResidentRepository repo) {
        this.repo = repo;
    }

    public List<CarteResident> findAll() {
        return repo.findAll();
    }

    public Optional<CarteResident> findById(Integer id) {
        return repo.findById(id);
    }

    public CarteResident getOrThrow(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte de résident introuvable"));
    }

    public Optional<CarteResident> findLastByReferencePrefix(String prefix) {
        return repo.findTopByReferenceStartingWithOrderByReferenceDesc(prefix);
    }

    public CarteResident create(CarteResident carteResident) {
        return repo.save(carteResident);
    }

    public CarteResident update(Integer id, CarteResident carteResident) {
        CarteResident existing = getOrThrow(id);
        existing.setDemande(carteResident.getDemande());
        existing.setReference(carteResident.getReference());
        existing.setDateDebut(carteResident.getDateDebut());
        existing.setDateFin(carteResident.getDateFin());
        existing.setPasseport(carteResident.getPasseport());
        return repo.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        repo.deleteById(id);
    }
}
