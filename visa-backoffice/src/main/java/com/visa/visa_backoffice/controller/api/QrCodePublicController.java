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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
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

        String base = resolveFrontOfficeBaseUrl();
        String url = (base.endsWith("/") ? base : base + "/") + demande.getQrToken();

        byte[] png = qrCodeService.generatePng(url, 320);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    /**
     * Génère un QR Code pour ouvrir directement le suivi par numéro de demande et/ou numéro de passeport.
     * Exemple de cible encodée: http://localhost:5174/#/suivi?demandeNumero=MADA-2026-0001
     */
    @GetMapping(value = "/ref.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> pngByRef(@RequestParam(required = false) String demandeNumero,
                                          @RequestParam(required = false) String passeportNumero) {
        String demandeNumeroTrim = demandeNumero == null ? null : demandeNumero.trim();
        String passeportNumeroTrim = passeportNumero == null ? null : passeportNumero.trim();

        if ((demandeNumeroTrim == null || demandeNumeroTrim.isBlank())
                && (passeportNumeroTrim == null || passeportNumeroTrim.isBlank())) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Paramètre obligatoire : demandeNumero ou passeportNumero");
        }

        String base = resolveFrontOfficeBaseUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String url;
        if (demandeNumeroTrim != null && !demandeNumeroTrim.isBlank()) {
            url = base + "?demandeNumero=" + URLEncoder.encode(demandeNumeroTrim, StandardCharsets.UTF_8);
            if (passeportNumeroTrim != null && !passeportNumeroTrim.isBlank()) {
                url += "&passeportNumero=" + URLEncoder.encode(passeportNumeroTrim, StandardCharsets.UTF_8);
            }
        } else {
            url = base + "?passeportNumero=" + URLEncoder.encode(passeportNumeroTrim, StandardCharsets.UTF_8);
        }

        byte[] png = qrCodeService.generatePng(url, 320);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    private String resolveFrontOfficeBaseUrl() {
        String base = frontOfficeBaseUrl == null ? "" : frontOfficeBaseUrl.trim();
        if (base.isBlank()) {
            return base;
        }

        // Si l'URL est configurée en localhost, on tente de la rendre scannable depuis un autre appareil
        // en remplaçant par une IP LAN. Fallback : on garde tel quel.
        if (base.contains("localhost") || base.contains("127.0.0.1")) {
            String ip = detectLanIpv4();
            if (ip != null && !ip.isBlank()) {
                base = base.replace("localhost", ip).replace("127.0.0.1", ip);
            }
        }
        return base;
    }

    private String detectLanIpv4() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            if (ifaces == null) {
                return null;
            }

            while (ifaces.hasMoreElements()) {
                NetworkInterface nif = ifaces.nextElement();
                try {
                    if (!nif.isUp() || nif.isLoopback() || nif.isVirtual()) {
                        continue;
                    }
                } catch (Exception ignored) {
                    continue;
                }

                Enumeration<InetAddress> addrs = nif.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!(addr instanceof Inet4Address)) {
                        continue;
                    }
                    if (addr.isLoopbackAddress() || addr.isAnyLocalAddress()) {
                        continue;
                    }
                    // on privilégie une IP LAN (192.168.x.x / 10.x.x.x / 172.16-31.x.x)
                    if (addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }

            // Dernier recours
            InetAddress local = InetAddress.getLocalHost();
            if (local instanceof Inet4Address && !local.isLoopbackAddress()) {
                return local.getHostAddress();
            }
        } catch (UnknownHostException ignored) {
            return null;
        } catch (Exception ignored) {
            return null;
        }

        return null;
    }
}
