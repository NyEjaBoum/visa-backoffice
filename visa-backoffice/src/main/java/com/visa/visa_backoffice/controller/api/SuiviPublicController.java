package com.visa.visa_backoffice.controller.api;

import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.HistoriqueStatutDemande;
import com.visa.visa_backoffice.repository.DemandeRepository;
import com.visa.visa_backoffice.repository.HistoriqueStatutDemandeRepository;
import com.visa.visa_backoffice.repository.PasseportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/suivi")
@CrossOrigin(origins = "*")
public class SuiviPublicController {

    private final DemandeRepository demandeRepository;
    private final PasseportRepository passeportRepository;
    private final HistoriqueStatutDemandeRepository historiqueRepository;

    public SuiviPublicController(DemandeRepository demandeRepository,
                                 PasseportRepository passeportRepository,
                                 HistoriqueStatutDemandeRepository historiqueRepository) {
        this.demandeRepository = demandeRepository;
        this.passeportRepository = passeportRepository;
        this.historiqueRepository = historiqueRepository;
    }

    @GetMapping("/{token}")
    public SuiviResponse getByToken(@PathVariable String token) {
        UUID uuid;
        try {
            uuid = UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token QR invalide.");
        }

        Demande demande = demandeRepository.findByQrToken(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable."));

        return buildResponse(demande);
    }

    @GetMapping
    public SuiviResponse getByQuery(@RequestParam(required = false) String demandeNumero,
                                   @RequestParam(required = false) String passeportNumero) {
        String demandeNumeroTrim = demandeNumero == null ? null : demandeNumero.trim();
        String passeportNumeroTrim = passeportNumero == null ? null : passeportNumero.trim();

        if ((demandeNumeroTrim == null || demandeNumeroTrim.isBlank())
                && (passeportNumeroTrim == null || passeportNumeroTrim.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Paramètre obligatoire : demandeNumero ou passeportNumero");
        }

        if (demandeNumeroTrim != null && !demandeNumeroTrim.isBlank()) {
            Demande demande = demandeRepository.findByNumDemande(demandeNumeroTrim)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable."));
            return buildResponse(demande);
        }

        var passeport = passeportRepository.findByNumero(passeportNumeroTrim)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passeport introuvable."));

        Integer demandeurId = passeport.getDemandeur() == null ? null : passeport.getDemandeur().getId();
        if (demandeurId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable.");
        }

        Demande demande = demandeRepository.findTopByDemandeur_IdOrderByDateCreationDesc(demandeurId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable."));

        return buildResponse(demande);
    }

    private SuiviResponse buildResponse(Demande demande) {
        List<HistoriqueStatutDemande> items = historiqueRepository
                .findByDemandeIdOrderByDateChangementAsc(demande.getId());

        List<SuiviEtapeResponse> historique = items.stream().map(h -> new SuiviEtapeResponse(
                h.getStatut() == null ? null : h.getStatut().getLibelle(),
                h.getDateChangement()
        )).toList();

        String statutActuel = null;
        if (!historique.isEmpty()) {
            statutActuel = historique.get(historique.size() - 1).statut();
        } else if (demande.getStatut() != null) {
            statutActuel = demande.getStatut().getLibelle();
        }

        String nom = demande.getDemandeur() == null ? null : demande.getDemandeur().getNom();
        String prenoms = demande.getDemandeur() == null ? null : demande.getDemandeur().getPrenoms();

        return new SuiviResponse(
                demande.getQrToken(),
                demande.getNumDemande(),
                demande.getDateCreation(),
                statutActuel,
                nom,
                prenoms,
                historique
        );
    }

    public record SuiviEtapeResponse(String statut, LocalDateTime dateHeure) {}

    public record SuiviResponse(UUID token,
                                String demandeNumero,
                                LocalDateTime demandeDateCreation,
                                String statutActuel,
                                String demandeurNom,
                                String demandeurPrenoms,
                                List<SuiviEtapeResponse> historique) {
    }
}
