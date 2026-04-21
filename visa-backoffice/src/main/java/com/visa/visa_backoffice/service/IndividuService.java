package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.IndividuAutocompleteDTO;
import com.visa.visa_backoffice.dto.IndividuCompletDTO;
import com.visa.visa_backoffice.model.Individu;
import com.visa.visa_backoffice.repository.IndividuAutocompleteRepository;
import com.visa.visa_backoffice.repository.IndividuCompletRepository;
import com.visa.visa_backoffice.repository.IndividuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IndividuService {

    private final IndividuRepository individuRepository;
    private final IndividuAutocompleteRepository individuAutocompleteRepository;
    private final IndividuCompletRepository individuCompletRepository;

    public IndividuService(
            IndividuRepository individuRepository,
            IndividuAutocompleteRepository individuAutocompleteRepository,
            IndividuCompletRepository individuCompletRepository
    ) {
        this.individuRepository = individuRepository;
        this.individuAutocompleteRepository = individuAutocompleteRepository;
        this.individuCompletRepository = individuCompletRepository;
    }

    // ===== CRUD =====

    public List<Individu> findAll() {
        return individuRepository.findAll();
    }

    public Optional<Individu> findById(Integer id) {
        return individuRepository.findById(id);
    }

    public Individu getOrThrow(Integer id) {
        return individuRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individu introuvable"));
    }

    public Individu create(Individu individu) {
        // ici tu peux mettre des trims/validations si besoin
        return individuRepository.save(individu);
    }

    public Individu update(Integer id, Individu individu) {
        Individu existing = getOrThrow(id);
        existing.setNom(individu.getNom());
        existing.setPrenoms(individu.getPrenoms());
        existing.setNomJeuneFille(individu.getNomJeuneFille());
        existing.setDateNaissance(individu.getDateNaissance());
        existing.setSituationFamiliale(individu.getSituationFamiliale());
        existing.setNationalite(individu.getNationalite());
        existing.setProfession(individu.getProfession());
        existing.setAdresseMada(individu.getAdresseMada());
        existing.setContactMada(individu.getContactMada());
        return individuRepository.save(existing);
    }

    public void delete(Integer id) {
        getOrThrow(id);
        individuRepository.deleteById(id);
    }

    // (optionnel) recherche simple JPA
    public List<Individu> searchByNomOrPrenom(String search) {
        return individuRepository.searchByNomOrPrenom(search);
    }

    // ===== Autocomplete (reprend ton IndividuAutocompleteService) =====

    public List<IndividuAutocompleteDTO> searchAutocomplete(String search) {
        List<Object[]> results = individuAutocompleteRepository.searchIndividuAutocomplete(search);
        List<IndividuAutocompleteDTO> dtos = new ArrayList<>();

        for (Object[] row : results) {
            IndividuAutocompleteDTO dto = new IndividuAutocompleteDTO();
            dto.setIndividuId(row[0] != null ? Long.valueOf(row[0].toString()) : null);
            dto.setNom((String) row[1]);
            dto.setPrenom((String) row[2]);
            dto.setDateNaissance(row[3] != null ? row[3].toString() : null);
            dto.setContact((String) row[4]);
            dto.setPasseportNumero((String) row[5]);
            dto.setDateDelivrance(row[6] != null ? row[6].toString() : null);
            dto.setDateExpiration(row[7] != null ? row[7].toString() : null);
            dto.setNomJeuneFille((String) row[8]);
            dto.setSituationFamilialeId(row[9] != null ? ((Number) row[9]).intValue() : null);
            dto.setNationalite((String) row[10]);
            dto.setProfession((String) row[11]);
            dto.setAdresseMada((String) row[12]);
            dtos.add(dto);
        }

        return dtos;
    }

    // ===== Complet (reprend ton IndividuCompletService) =====

    public List<IndividuCompletDTO> listComplets() {
        return individuCompletRepository.findAllComplets().stream()
                .map(this::toDto)
                .toList();
    }

    public IndividuCompletDTO getComplet(Integer id) {
        return individuCompletRepository.findCompletById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Individu introuvable"));
    }

    private IndividuCompletDTO toDto(IndividuCompletRepository.IndividuCompletProjection p) {
        return new IndividuCompletDTO(
                p.getIndividuId(),
                p.getNom(),
                p.getPrenom(),
                p.getNomJeuneFille(),
                p.getDateNaissance(),
                p.getSituationFamiliale(),
                p.getNationalite(),
                p.getProfession(),
                p.getAdresseMada(),
                p.getContact(),
                p.getPasseportNumero(),
                p.getDateDelivrance(),
                p.getDateExpiration()
        );
    }
}