package com.visa.visa_backoffice.controller.api;

import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.repository.DemandeRepository;
import com.visa.visa_backoffice.service.QrCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/qr")
@CrossOrigin(origins = "*")
public class QrCodePublicController {

    private final DemandeRepository demandeRepository;
    private final QrCodeService qrCodeService;

    @Value("${app.frontoffice.base-url:http://localhost:5174/#/suivi/}")
    private String frontOfficeBaseUrl;

    public QrCodePublicController(DemandeRepository demandeRepository, QrCodeService qrCodeService) {
        this.demandeRepository = demandeRepository;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping(value = "/{token}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> png(@PathVariable String token) {
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Token QR invalide.");
        }

        Demande demande = demandeRepository.findByQrToken(uuid)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Demande introuvable."));

        String base = frontOfficeBaseUrl == null ? "" : frontOfficeBaseUrl.trim();
        String url = (base.endsWith("/") ? base : base + "/") + demande.getQrToken();

        byte[] png = qrCodeService.generatePng(url, 320);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }
}
