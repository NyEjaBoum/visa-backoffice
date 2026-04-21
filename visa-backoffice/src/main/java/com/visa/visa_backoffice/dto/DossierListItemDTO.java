package com.visa.visa_backoffice.dto;

import java.time.LocalDateTime;

public record DossierListItemDTO(
        Integer id,
        String numDemande,
        String nom,
        String prenoms,
        String typeVisa,
        String typeDemande,
        String statut,
        LocalDateTime dateCreation
) {}
