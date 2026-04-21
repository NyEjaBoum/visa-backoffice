package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.IndividuCompletDTO;
import com.visa.visa_backoffice.repository.IndividuCompletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class IndividuCompletService {

    private final IndividuCompletRepository individuCompletRepository;

    public IndividuCompletService(IndividuCompletRepository individuCompletRepository) {
        this.individuCompletRepository = individuCompletRepository;
    }

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
