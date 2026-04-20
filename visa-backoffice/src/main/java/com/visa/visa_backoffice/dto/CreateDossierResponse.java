package com.visa.visa_backoffice.dto;

public record CreateDossierResponse(
        Integer dossierId,
        String numDemande,
        Integer statutId
) {}
