package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.Individu;
import com.visa.visa_backoffice.service.IndividuService;
import com.visa.visa_backoffice.service.IndividuAutocompleteService;
import com.visa.visa_backoffice.dto.IndividuAutocompleteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/individus")
@CrossOrigin
public class IndividuController {

    @Autowired
    private IndividuService individuService;

    @Autowired
    private IndividuAutocompleteService individuAutocompleteService;

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
        return individuService.save(individu);
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<Individu> update(@PathVariable Long id, @RequestBody Individu individu) {
    //     if (!individuService.findById(id).isPresent()) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     individu.setId(id);
    //     return ResponseEntity.ok(individuService.save(individu));
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!individuService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        individuService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<IndividuAutocompleteDTO> search(@RequestParam String q) {
        return individuAutocompleteService.search(q);
    }
}
