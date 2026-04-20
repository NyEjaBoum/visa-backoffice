package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.CreateDossierRequest;
import com.visa.visa_backoffice.dto.CreateDossierResponse;
import com.visa.visa_backoffice.dto.DashboardStatsResponse;
import com.visa.visa_backoffice.service.DossierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dossiers")
public class DossierController {

    private final DossierService dossierService;

    public DossierController(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    @PostMapping
    public ResponseEntity<CreateDossierResponse> create(@RequestBody CreateDossierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dossierService.createDossier(request));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        int created = dossierService.countDossiersByStatut(1);
        int validated = dossierService.countDossiersByStatut(10);
        int cancelled = dossierService.countDossiersByStatut(20);
        return ResponseEntity.ok(new DashboardStatsResponse(created, validated, cancelled));
    }
}
