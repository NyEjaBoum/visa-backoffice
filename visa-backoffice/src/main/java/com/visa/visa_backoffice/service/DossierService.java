package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.CreateDossierRequest;
import com.visa.visa_backoffice.dto.CreateDossierResponse;
import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DossierService {

    private static final int STATUT_CREE_ID = 1;

    private final IndividuRepository individuRepository;
    private final PasseportRepository passeportRepository;
    private final DemandeVisaRepository demandeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final StatutRepository statutRepository;

    public DossierService(IndividuRepository individuRepository,
                          PasseportRepository passeportRepository,
                          DemandeVisaRepository demandeVisaRepository,
                          TypeDemandeRepository typeDemandeRepository,
                          TypeVisaRepository typeVisaRepository,
                          StatutRepository statutRepository) {
        this.individuRepository = individuRepository;
        this.passeportRepository = passeportRepository;
        this.demandeVisaRepository = demandeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.statutRepository = statutRepository;
    }

    @Transactional
    public CreateDossierResponse createDossier(CreateDossierRequest request) {
        validateRequest(request);

        TypeDemande typeDemande = typeDemandeRepository.findById(request.typeDemandeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de demande introuvable"));

        TypeVisa typeVisa = typeVisaRepository.findById(request.typeVisaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de visa introuvable"));

        Statut statutCree = statutRepository.findById(STATUT_CREE_ID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Statut CREE non configure"));

        Individu individu = new Individu();
        individu.setNom(request.nom().trim());
        individu.setPrenoms(safeTrim(request.prenoms()));
        individu.setNomJeuneFille(safeTrim(request.nomJeuneFille()));
        individu.setDateNaissance(request.dateNaissance());
        individu.setSituationFamiliale(safeTrim(request.situationFamiliale()));
        individu.setNationalite(safeTrim(request.nationalite()));
        individu.setProfession(safeTrim(request.profession()));
        individu.setAdresseMada(safeTrim(request.adresseMada()));
        individu.setContactMada(safeTrim(request.contactMada()));
        Individu savedIndividu = individuRepository.save(individu);

        Passeport passeport = new Passeport();
        passeport.setIndividu(savedIndividu);
        passeport.setNumeroPass(request.numeroPasseport().trim());
        passeport.setDateDelivrance(request.dateDelivrancePasseport());
        passeport.setDateExpiration(request.dateExpirationPasseport());
        passeport.setActive(true);
        Passeport savedPasseport = passeportRepository.save(passeport);

        DemandeVisa dossier = new DemandeVisa();
        dossier.setNumDemande(generateNumeroDossier());
        dossier.setIndividu(savedIndividu);
        dossier.setPasseport(savedPasseport);
        dossier.setTypeDemande(typeDemande);
        dossier.setTypeVisa(typeVisa);
        dossier.setStatut(statutCree);
        dossier.setRefVisaTrans(request.refVisaTransformable().trim());
        dossier.setDateEntree(request.dateEntree());
        dossier.setLieuEntree(safeTrim(request.lieuEntree()));
        dossier.setDateFinVisa(request.dateFinVisa());
        dossier.setDateCreation(LocalDateTime.now());

        DemandeVisa savedDossier = demandeVisaRepository.save(dossier);

        return new CreateDossierResponse(
                savedDossier.getId(),
                savedDossier.getNumDemande(),
                statutCree.getId()
        );
    }

    private void validateRequest(CreateDossierRequest request) {
        if (request.typeDemandeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de demande est obligatoire");
        }
        if (request.typeVisaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categorie est obligatoire");
        }
        if (isBlank(request.nom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom est obligatoire");
        }
        if (request.dateNaissance() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de naissance est obligatoire");
        }
        if (isBlank(request.numeroPasseport())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numero de passeport est obligatoire");
        }
        if (isBlank(request.refVisaTransformable())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reference du visa transformable est obligatoire");
        }
    }

    private String generateNumeroDossier() {
        int year = LocalDate.now().getYear();
        String prefix = "MADA-" + year + "-";

        int nextIndex = demandeVisaRepository.findTopByNumDemandeStartingWithOrderByNumDemandeDesc(prefix)
                .map(d -> d.getNumDemande().substring(prefix.length()))
                .map(this::parseSafely)
                .orElse(0) + 1;

        return prefix + String.format("%04d", nextIndex);
    }

    private int parseSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    public int countDossiersByStatut(int statutId) {
        return (int) demandeVisaRepository.findAll().stream()
                .filter(d -> d.getStatut() != null && d.getStatut().getId() == statutId)
                .count();
    }
}
