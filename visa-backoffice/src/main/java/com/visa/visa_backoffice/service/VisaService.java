package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.model.Visa;
import com.visa.visa_backoffice.repository.VisaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VisaService {

    private final VisaRepository visaRepository;

    public VisaService(VisaRepository visaRepository) {
        this.visaRepository = visaRepository;
    }

    @Transactional(readOnly = true)
    public List<Visa> findAll() {
        return visaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Visa findByIdOrThrow(Integer id) {
        return visaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visa introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Visa> findByNumero(String numero) {
        if (numero == null || numero.isBlank()) return Optional.empty();
        return visaRepository.findByNumero(numero.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Visa> findByDemande(Demande demande) {
        if (demande == null || demande.getId() == null) return Optional.empty();
        return visaRepository.findByDemandeId(demande.getId());
    }

    /**
     * Crée un nouveau visa lié à une demande et un passeport
     * Vérifie que le numéro n'existe pas déjà
     */
    public Visa create(Visa visa, Demande demande, Passeport passeport) {
        if (visa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visa obligatoire.");
        }

        if (visa.getNumero() != null && !visa.getNumero().isBlank()) {
            Optional<Visa> existing = findByNumero(visa.getNumero());
            if (existing.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ce numéro de visa existe déjà.");
            }
        }

        visa.setDemande(demande);
        visa.setPasseport(passeport);
        return visaRepository.save(visa);
    }

    /**
     * Sauvegarde un visa (create/update)
     */
    public Visa save(Visa visa) {
        return visaRepository.save(visa);
    }

    /**
     * Met à jour un visa existant
     */
    public Visa update(Integer id, Visa payload) {
        Visa existing = findByIdOrThrow(id);
        existing.setNumero(payload.getNumero());
        existing.setDateDebut(payload.getDateDebut());
        existing.setDateFin(payload.getDateFin());
        return visaRepository.save(existing);
    }

    /**
     * Supprime un visa
     */
    @Transactional
    public void delete(Integer id) {
        Visa visa = findByIdOrThrow(id);
        visaRepository.delete(visa);
    }
}
