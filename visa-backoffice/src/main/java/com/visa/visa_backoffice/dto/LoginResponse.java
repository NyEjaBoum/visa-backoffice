package com.visa.visa_backoffice.dto;

import java.util.List;

public record LoginResponse(
    String token,
    Integer userId,
    String identifiant,
    String role,
    List<String> permissions,
    List<NomenclatureItem> statuts,
    List<NomenclatureItem> typeVisa,
    List<NomenclatureItem> typeDemande,
    List<NomenclatureItem> situationsFamiliales
) {}
