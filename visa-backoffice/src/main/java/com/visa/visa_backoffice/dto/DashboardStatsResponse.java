package com.visa.visa_backoffice.dto;

public record DashboardStatsResponse(
        Integer dossiersCreated,
        Integer dossiersValidated,
        Integer dossiersCancelled
) {}
