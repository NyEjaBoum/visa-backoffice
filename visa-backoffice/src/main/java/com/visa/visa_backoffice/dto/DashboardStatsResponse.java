package com.visa.visa_backoffice.dto;

public record DashboardStatsResponse(
        Integer total,
        Integer crees,
        Integer valides,
        Integer annules
) {}
