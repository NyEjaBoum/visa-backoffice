package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.CarteResident;
import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.repository.CarteResidentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarteResidentService {

    private final CarteResidentRepository carteResidentRepository;

    public CarteResidentService(CarteResidentRepository carteResidentRepository) {
        this.carteResidentRepository = carteResidentRepository;
    }

    @Transactional(readOnly = true)
    public List<CarteResident> findAll() {
        return carteResidentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CarteResident findByIdOrThrow(Integer id) {
        return carteResidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carte résident introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Optional<CarteResident> findByNumero(String numero) {
        if (numero == null || numero.isBlank()) return Optional.empty();
        return carteResidentRepository.findByNumero(numero.trim());
    }

    @Transactional(readOnly = true)
    public Optional<CarteResident> findByDemande(Demande demande) {
        if (demande == null || demande.getId() == null) return Optional.empty();
        return carteResidentRepository.findByDemandeId(demande.getId());
    }

    /**
     * Crée une nouvelle carte résident liée à une demande et un passeport
     * Vérifie que le numéro n'existe pas déjà
     */
    public CarteResident create(CarteResident carte, Demande demande, Passeport passeport) {
        if (carte == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carte résident obligatoire.");
        }

        if (carte.getNumero() != null && !carte.getNumero().isBlank()) {
            Optional<CarteResident> existing = findByNumero(carte.getNumero());
            if (existing.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ce numéro de carte résident existe déjà.");
            }
        }

        carte.setDemande(demande);
        carte.setPasseport(passeport);
        return carteResidentRepository.save(carte);
    }

    /**
     * Sauvegarde une carte résident (create/update)
     */
    public CarteResident save(CarteResident carte) {
        return carteResidentRepository.save(carte);
    }

    /**
     * Met à jour une carte résident existante
     */
    public CarteResident update(Integer id, CarteResident payload) {
        CarteResident existing = findByIdOrThrow(id);
        existing.setNumero(payload.getNumero());
        existing.setDateDebut(payload.getDateDebut());
        existing.setDateFin(payload.getDateFin());
        return carteResidentRepository.save(existing);
    }

    /**
     * Supprime une carte résident
     */
    @Transactional
    public void delete(Integer id) {
        CarteResident carte = findByIdOrThrow(id);
        carteResidentRepository.delete(carte);
    }
}
