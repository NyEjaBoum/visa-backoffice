package com.visa.visa_backoffice.dto;

import java.time.LocalDate;

public record IndividuCompletDTO(
        Integer individuId,
        String nom,
        String prenom,
        String nomJeuneFille,
        LocalDate dateNaissance,
        String situationFamiliale,
        String nationalite,
        String profession,
        String adresseMada,
        String contact,
        String passeportNumero,
        LocalDate dateDelivrance,
        LocalDate dateExpiration
) {}
