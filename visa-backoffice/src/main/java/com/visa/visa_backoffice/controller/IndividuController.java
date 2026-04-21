package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.IndividuCompletDTO;
import com.visa.visa_backoffice.model.Individu;
import com.visa.visa_backoffice.service.IndividuService;
import com.visa.visa_backoffice.dto.IndividuAutocompleteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/individus")
@CrossOrigin
public class IndividuController {

    private final IndividuService individuService;

    public IndividuController(IndividuService individuService) {
        this.individuService = individuService;
    }

    @GetMapping
    public List<Individu> getAll() {
        return individuService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Individu> getById(@PathVariable Integer id) {
        Optional<Individu> individu = individuService.findById(id);
        return individu.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Individu create(@RequestBody Individu individu) {
        return individuService.create(individu);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Individu> update(@PathVariable Integer id, @RequestBody Individu individu) {
        return ResponseEntity.ok(individuService.update(id, individu));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        individuService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<IndividuAutocompleteDTO> search(@RequestParam String q) {
        return individuService.searchAutocomplete(q);
    }

    @GetMapping("/complets")
    public List<IndividuCompletDTO> getComplets() {
        return individuService.listComplets();
    }

    @GetMapping("/{id}/complet")
    public ResponseEntity<IndividuCompletDTO> getComplet(@PathVariable Integer id) {
        return ResponseEntity.ok(individuService.getComplet(id));
    }
}
