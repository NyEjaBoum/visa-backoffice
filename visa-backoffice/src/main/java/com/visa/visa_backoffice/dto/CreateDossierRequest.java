package com.visa.visa_backoffice.dto;

import java.time.LocalDate;

public record CreateDossierRequest(
        Integer typeDemandeId,
        Integer typeVisaId,
        String nom,
        String prenoms,
        String nomJeuneFille,
        LocalDate dateNaissance,
        String situationFamiliale,
        String nationalite,
        String profession,
        String adresseMada,
        String contactMada,
        String numeroPasseport,
        LocalDate dateDelivrancePasseport,
        LocalDate dateExpirationPasseport,
        String refVisaTransformable,
        LocalDate dateEntree,
        String lieuEntree,
        LocalDate dateFinVisa
) {}
