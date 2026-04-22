package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.CreateDossierRequest;
import com.visa.visa_backoffice.dto.CreateDossierResponse;
import com.visa.visa_backoffice.dto.DashboardStatsResponse;
import com.visa.visa_backoffice.dto.DossierDetailDTO;
import com.visa.visa_backoffice.dto.DossierListItemDTO;
import com.visa.visa_backoffice.dto.ValiderDossierResponse;
import com.visa.visa_backoffice.service.DossierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}/valider")
    public ResponseEntity<ValiderDossierResponse> valider(@PathVariable Integer id) {
        return ResponseEntity.ok(dossierService.validerDossier(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(dossierService.getStats());
    }

    @GetMapping
    public ResponseEntity<List<DossierListItemDTO>> list() {
        return ResponseEntity.ok(dossierService.listDossiers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DossierDetailDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(dossierService.getDossier(id));
    }
}
