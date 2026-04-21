package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.NomenclatureItem;
import com.visa.visa_backoffice.repository.PieceTypeRepository;
import com.visa.visa_backoffice.repository.TypeVisaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nomenclatures")
public class NomenclatureController {

    private final TypeVisaRepository typeVisaRepository;
    private final PieceTypeRepository pieceTypeRepository;

    public NomenclatureController(TypeVisaRepository typeVisaRepository,
                                   PieceTypeRepository pieceTypeRepository) {
        this.typeVisaRepository = typeVisaRepository;
        this.pieceTypeRepository = pieceTypeRepository;
    }

    @GetMapping("/type-visa")
    public ResponseEntity<List<NomenclatureItem>> getTypeVisa() {
        List<NomenclatureItem> result = typeVisaRepository.findAll().stream()
                .map(t -> new NomenclatureItem(t.getId(), t.getLibelle()))
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pieces/communes")
    public ResponseEntity<List<String>> getPiecesCommunes() {
        List<String> result = pieceTypeRepository.findByTypeVisaIsNull().stream()
                .map(p -> p.getLibelle())
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pieces/{typeVisaId}")
    public ResponseEntity<List<String>> getPiecesByTypeVisa(@PathVariable Integer typeVisaId) {
        List<String> result = pieceTypeRepository.findByTypeVisaId(typeVisaId).stream()
                .map(p -> p.getLibelle())
                .toList();
        return ResponseEntity.ok(result);
    }
}
