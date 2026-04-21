package com.visa.visa_backoffice.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateDossierRequest(
        Integer typeDemandeId,
        Integer typeVisaId,
        String nom,
        String prenoms,
        String nomJeuneFille,
        LocalDate dateNaissance,
        Integer situationFamilialeId,
        String nationalite,
        String profession,
        String adresseMada,
        String contactMada,
        String numeroPasseport,
        LocalDate dateDelivrancePasseport,
        LocalDate dateExpirationPasseport,
        String numeroVisaTransformable,
        LocalDate dateEntree,
        String lieuEntree,
        LocalDate dateFinVisa,
        List<PieceFournieItem> piecesFournies
) {}
