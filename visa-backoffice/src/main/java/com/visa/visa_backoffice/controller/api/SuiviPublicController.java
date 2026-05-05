package com.visa.visa_backoffice.controller.api;

import com.visa.visa_backoffice.model.Demande;
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

    // ─── Recherche par QR token (vue détail unique) ───────────────────────────

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

        return buildSuiviResponse(demande);
    }

    // ─── Recherche par numéro de demande ou passeport (vue liste) ─────────────

    @GetMapping
    public SuiviListResponse getByQuery(@RequestParam(required = false) String demandeNumero,
                                        @RequestParam(required = false) String passeportNumero) {
        String dnTrim = demandeNumero == null ? null : demandeNumero.trim();
        String pnTrim = passeportNumero == null ? null : passeportNumero.trim();

        if ((dnTrim == null || dnTrim.isBlank()) && (pnTrim == null || pnTrim.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Paramètre obligatoire : demandeNumero ou passeportNumero");
        }

        if (dnTrim != null && !dnTrim.isBlank()) {
            return rechercherParNumDemande(dnTrim);
        }

        return rechercherParPasseport(pnTrim);
    }

    private SuiviListResponse rechercherParNumDemande(String numDemande) {
        Demande principale = demandeRepository.findByNumDemande(numDemande)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable."));

        Integer demandeurId = principale.getDemandeur().getId();
        List<Demande> toutes = demandeRepository.findAllByDemandeurIdWithRefs(demandeurId);

        String nom = principale.getDemandeur().getNom();
        String prenoms = principale.getDemandeur().getPrenoms();

        List<SuiviDemandeItem> items = toutes.stream().map(this::buildDemandeItem).toList();

        return new SuiviListResponse(nom, prenoms, numDemande, items);
    }

    private SuiviListResponse rechercherParPasseport(String passeportNumero) {
        var passeport = passeportRepository.findByNumero(passeportNumero)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passeport introuvable."));

        Integer demandeurId = passeport.getDemandeur() == null ? null : passeport.getDemandeur().getId();
        if (demandeurId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun demandeur lié à ce passeport.");
        }

        List<Demande> toutes = demandeRepository.findAllByDemandeurIdWithRefs(demandeurId);
        if (toutes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune demande trouvée.");
        }

        Demande premiere = toutes.get(0);
        String nom = premiere.getDemandeur().getNom();
        String prenoms = premiere.getDemandeur().getPrenoms();
        String selected = premiere.getNumDemande();

        List<SuiviDemandeItem> items = toutes.stream().map(this::buildDemandeItem).toList();

        return new SuiviListResponse(nom, prenoms, selected, items);
    }

    // ─── Builders ─────────────────────────────────────────────────────────────

    private SuiviResponse buildSuiviResponse(Demande demande) {
        List<SuiviEtapeResponse> historique = buildHistorique(demande.getId());

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

    private SuiviDemandeItem buildDemandeItem(Demande demande) {
        List<SuiviEtapeResponse> historique = buildHistorique(demande.getId());

        String statutActuel = null;
        if (!historique.isEmpty()) {
            statutActuel = historique.get(historique.size() - 1).statut();
        } else if (demande.getStatut() != null) {
            statutActuel = demande.getStatut().getLibelle();
        }

        return new SuiviDemandeItem(
                demande.getQrToken(),
                demande.getNumDemande(),
                demande.getDateCreation(),
                statutActuel,
                historique
        );
    }

    private List<SuiviEtapeResponse> buildHistorique(Integer demandeId) {
        return historiqueRepository
                .findByDemandeIdOrderByDateChangementAsc(demandeId)
                .stream()
                .map(h -> new SuiviEtapeResponse(
                        h.getStatut() == null ? null : h.getStatut().getLibelle(),
                        h.getDateChangement()
                ))
                .toList();
    }

    // ─── Response records ─────────────────────────────────────────────────────

    public record SuiviEtapeResponse(String statut, LocalDateTime dateHeure) {}

    public record SuiviResponse(
            UUID token,
            String demandeNumero,
            LocalDateTime demandeDateCreation,
            String statutActuel,
            String demandeurNom,
            String demandeurPrenoms,
            List<SuiviEtapeResponse> historique) {}

    public record SuiviDemandeItem(
            UUID token,
            String demandeNumero,
            LocalDateTime demandeDateCreation,
            String statutActuel,
            List<SuiviEtapeResponse> historique) {}

    public record SuiviListResponse(
            String demandeurNom,
            String demandeurPrenoms,
            String demandeSelectionneeNumero,
            List<SuiviDemandeItem> demandes) {}
}
