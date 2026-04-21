package com.visa.visa_backoffice.dto;

import java.time.LocalDate;

public record ValiderDossierResponse(
        Integer dossierId,
        String numDemande,
        Integer carteResidentId,
        String referenceCarteResident,
        LocalDate dateDebut,
        LocalDate dateFin
) {}
