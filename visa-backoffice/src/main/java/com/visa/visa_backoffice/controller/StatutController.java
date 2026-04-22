package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.Statut;
import com.visa.visa_backoffice.service.StatutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts")
public class StatutController {

    private final StatutService statutService;

    public StatutController(StatutService statutService) {
        this.statutService = statutService;
    }

    @GetMapping
    public ResponseEntity<List<Statut>> list() {
        return ResponseEntity.ok(statutService.findAll());
    }
}
