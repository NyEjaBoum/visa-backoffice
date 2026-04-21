package com.visa.visa_backoffice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DossierDetailDTO(
        Integer id,
        String numDemande,
        String typeVisa,
        String typeDemande,
        String statut,
        LocalDateTime dateCreation,
        Integer individuId,
        String individuNom,
        String individuPrenoms,
        LocalDate individuDateNaissance,
        String passeportNumero,
        LocalDate passeportDateDelivrance,
        LocalDate passeportDateExpiration,
        String visaTransformableReference,
        LocalDate visaTransformableDateEntree,
        String visaTransformableLieuEntree,
        LocalDate visaTransformableDateFinVisa,
        List<PieceFournieDTO> pieces
) {}
