package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.DemandeForm;
import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeService demandeService;
    private final NomenclatureService nomenclatureService;
    private final PasseportService passeportService;
    private final PieceFournieService pieceFournieService;
    private final PieceJustificativeService pieceJustificativeService;
    private final PieceFichierService pieceFichierService;
    private final PdfService pdfService;
    private final StatutService statutService;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public DemandeController(DemandeService demandeService,
                            StatutService statutService,
                            NomenclatureService nomenclatureService,
                            PasseportService passeportService,
                            PieceFournieService pieceFournieService,
                            PieceJustificativeService pieceJustificativeService,
                            PieceFichierService pieceFichierService,
                            PdfService pdfService) {
        this.demandeService = demandeService;
        this.statutService = statutService;
        this.nomenclatureService = nomenclatureService;
        this.passeportService = passeportService;
        this.pieceFournieService = pieceFournieService;
        this.pieceJustificativeService = pieceJustificativeService;
        this.pieceFichierService = pieceFichierService;
        this.pdfService = pdfService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LISTE & FORMULAIRES
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("demandes", demandeService.findAll());
        return "demandes/liste";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("form", new DemandeForm());
        chargerNomenclatures(model);
        model.addAttribute("piecesJustificatives", pieceJustificativeService.findAll());
        return "demandes/formulaire";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS NORMAL — CRÉATION
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    public String creer(@ModelAttribute("form") DemandeForm form,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            form.validateOrThrow();

            Demande demande = demandeService.createComplet(
                    construireDemandeur(form),
                    construirePasseport(form),
                    construireVT(form),
                    nomenclatureService.findTypeVisa(form.getTypeVisaId()),
                    nomenclatureService.findTypeDemande(form.getTypeDemandeId()),
                    form.getPiecesFourniesIds(),
                    statutService.getStatutCree()
            );

            if (request instanceof MultipartHttpServletRequest mReq) {
                uploadFichiersCreation(demande.getId(), mReq.getFileMap(), redirectAttrs);
            }

            redirectAttrs.addFlashAttribute("successMessage",
                    "Demande créée avec succès. Numéro : " + demande.getNumDemande());
            return "redirect:/demandes/" + demande.getId();

        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            chargerNomenclatures(model);
            model.addAttribute("piecesJustificatives", pieceJustificativeService.findAll());
            return "demandes/formulaire";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS RATTRAPAGE — SANS DONNÉES ANTÉRIEURES
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/rattrapage")
    public String creerRattrapage(@ModelAttribute("form") DemandeForm form,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttrs,
                                  Model model) {
        try {
            form.validateOrThrow();

            Demande demande = demandeService.createRattrapageComplet(
                    construireDemandeur(form),
                    construirePasseport(form),
                    construireVT(form),
                    nomenclatureService.findTypeVisa(form.getTypeVisaId()),
                    nomenclatureService.findTypeDemande(form.getTypeDemandeId()),
                    nomenclatureService.findTypeDemandeNouveauTitre(),
                    construireVisaInjecte(form),
                    construireCarteInjectee(form));

            if (request instanceof MultipartHttpServletRequest mReq) {
                uploadFichiersCreation(demande.getId(), mReq.getFileMap(), redirectAttrs);
            }

            redirectAttrs.addFlashAttribute("successMessage",
                    "Demande créée avec succès (rattrapage). Numéro : " + demande.getNumDemande());
            return "redirect:/demandes/" + demande.getId();

        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            chargerNomenclatures(model);
            model.addAttribute("piecesJustificatives", pieceJustificativeService.findAll());
            return "demandes/formulaire";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DÉTAIL & MODIFICATION
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Demande demande = demandeService.findByIdOrThrow(id);
        model.addAttribute("demande", demande);

        Passeport passeport = passeportService.findLastForDemandeur(
                demande.getDemandeur() == null ? null : demande.getDemandeur().getId()
        ).orElse(null);
        model.addAttribute("passeport", passeport);

        List<PieceFournie> piecesFournies = pieceFournieService.findAllForDemande(id);
        model.addAttribute("piecesFournies", piecesFournies);

        Map<Integer, List<?>> fichiersByPiece = new LinkedHashMap<>();
        for (PieceFournie pf : piecesFournies) {
            fichiersByPiece.put(pf.getId(), pieceFichierService.findByPieceFournieId(pf.getId()));
        }
        model.addAttribute("fichiersByPiece", fichiersByPiece);

        String statutLibelle = demande.getStatut() != null ? demande.getStatut().getLibelle() : "";
        boolean modifiable = "CREE".equalsIgnoreCase(statutLibelle);
        boolean scanTermine = "SCAN TERMINÉ".equalsIgnoreCase(statutLibelle);
        model.addAttribute("modifiable", modifiable);
        model.addAttribute("scanTermine", scanTermine);

        boolean toutesPresentes = !piecesFournies.isEmpty()
                && piecesFournies.stream().allMatch(pf -> Boolean.TRUE.equals(pf.getIsPresent()));
        boolean toutesLesPiecesScannees = toutesPresentes
                && pieceFichierService.toutesLesPiecesOntUnFichier(piecesFournies);
        model.addAttribute("toutesLesPiecesScannees", toutesLesPiecesScannees);

        return "demandes/detail";
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        Demande demande = demandeService.findByIdOrThrow(id);

        if (demande.getStatut() == null || !"CREE".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            return "redirect:/demandes/" + id;
        }

        Passeport passeport = passeportService.findLastForDemandeur(
                demande.getDemandeur() == null ? null : demande.getDemandeur().getId()
        ).orElse(null);

        DemandeForm form = buildFormFromDemande(demande, passeport);
        form.setPiecesFourniesIds(pieceFournieService.findPresentPieceIds(id));

        model.addAttribute("form", form);
        model.addAttribute("demandeId", id);
        chargerNomenclatures(model);

        Integer typeVisaId = demande.getTypeVisa() != null ? demande.getTypeVisa().getId() : null;
        model.addAttribute("piecesJustificatives", pieceJustificativeService.findForTypeVisa(typeVisaId));

        chargerDonneesUpload(id, model);
        return "demandes/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("form") DemandeForm form,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            form.validateOrThrow();

            Demande demande = demandeService.findByIdOrThrow(id);

            demandeService.updateComplet(
                    demande,
                    construireDemandeur(form),
                    construirePasseport(form),
                    construireVT(form),
                    nomenclatureService.findTypeVisa(form.getTypeVisaId()),
                    nomenclatureService.findTypeDemande(form.getTypeDemandeId()),
                    form.getPiecesFourniesIds(),
                    null
            );

            redirectAttrs.addFlashAttribute("successMessage", "Demande mise à jour avec succès.");
            return "redirect:/demandes/" + id;

        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            model.addAttribute("demandeId", id);
            chargerNomenclatures(model);
            model.addAttribute("piecesJustificatives",
                    pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()));
            chargerDonneesUpload(id, model);
            return "demandes/formulaire";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPRINT 3 — UPLOAD FICHIERS
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/pieces/{pfId}/upload")
    public String uploadFichier(@PathVariable Integer id,
                                @PathVariable Integer pfId,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttrs) {
        try {
            pieceFichierService.upload(pfId, file);
            redirectAttrs.addFlashAttribute("successMessage", "Fichier ajouté avec succès.");
        } catch (ResponseStatusException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getReason());
        }
        return "redirect:/demandes/" + id;
    }

    @PostMapping("/{id}/fichiers/{fichierId}/supprimer")
    public String supprimerFichier(@PathVariable Integer id,
                                   @PathVariable Integer fichierId,
                                   RedirectAttributes redirectAttrs) {
        try {
            pieceFichierService.supprimer(fichierId);
            redirectAttrs.addFlashAttribute("successMessage", "Fichier supprimé.");
        } catch (ResponseStatusException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getReason());
        }
        return "redirect:/demandes/" + id;
    }

    @GetMapping("/fichiers/{fichierId}/telecharger")
    public ResponseEntity<Resource> telechargerFichier(@PathVariable Integer fichierId) {
        var pf = pieceFichierService.findByIdOrThrow(fichierId);
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(pf.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier non trouvé sur le serveur.");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + pf.getNomOriginal() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chemin de fichier invalide.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPRINT 3 — FINALISATION & PDF
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/finaliser-scan")
    public String finaliserScan(@PathVariable Integer id,
                                @ModelAttribute("form") DemandeForm form,
                                RedirectAttributes redirectAttrs,
                                Model model) {
        try {
            form.validateOrThrow();

            Demande demande = demandeService.findByIdOrThrow(id);

            demandeService.finaliserScan(
                    demande,
                    construireDemandeur(form),
                    construirePasseport(form),
                    construireVT(form),
                    nomenclatureService.findTypeVisa(form.getTypeVisaId()),
                    nomenclatureService.findTypeDemande(form.getTypeDemandeId()),
                    form.getPiecesFourniesIds()
            );

            redirectAttrs.addFlashAttribute("successMessage",
                    "Scan finalisé ! Le dossier est désormais verrouillé en lecture seule.");
            return "redirect:/demandes/" + id;

        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            model.addAttribute("demandeId", id);
            chargerNomenclatures(model);
            model.addAttribute("piecesJustificatives",
                    pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()));
            chargerDonneesUpload(id, model);
            return "demandes/formulaire";
        }
    }

    @GetMapping("/{id}/recepisse.pdf")
    public ResponseEntity<byte[]> telechargerRecepisse(@PathVariable Integer id) {
        Demande demande = demandeService.findByIdOrThrow(id);
        Passeport passeport = passeportService.findLastForDemandeur(
                demande.getDemandeur() == null ? null : demande.getDemandeur().getId()
        ).orElse(null);
        List<PieceFournie> piecesPresentes = pieceFournieService.findAllForDemande(id)
                .stream()
                .filter(pf -> Boolean.TRUE.equals(pf.getIsPresent()))
                .collect(Collectors.toList());

        byte[] pdf = pdfService.genererRecepisse(demande, passeport, piecesPresentes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"recepisse-" + demande.getNumDemande() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AUTOCOMPLETE
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/antecedents/autocomplete")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> autocomplete(
            @RequestParam(required = false, defaultValue = "") String q) {
        if (q.isBlank()) return ResponseEntity.ok(List.of());
        List<DossierComplet> resultats = demandeService.rechercherAntecedents(q.trim());
        List<Map<String, Object>> items = resultats.stream().map(d -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("demandeurId", d.getDemandeurId());
            item.put("nom", d.getNom() != null ? d.getNom() : "");
            item.put("prenoms", d.getPrenoms() != null ? d.getPrenoms() : "");
            item.put("nomJeuneFille", d.getNomJeuneFille() != null ? d.getNomJeuneFille() : "");
            item.put("profession", d.getProfession() != null ? d.getProfession() : "");
            item.put("nationalite", d.getNationalite() != null ? d.getNationalite() : "");
            item.put("nationaliteId", d.getNationaliteId());
            item.put("situationFamiliale", d.getSituationFamiliale() != null ? d.getSituationFamiliale() : "");
            item.put("situationFamilialeId", d.getSituationFamilialeId());
            item.put("dateNaissance", d.getDateNaissance() != null ? d.getDateNaissance().toString() : "");
            item.put("adresseMada", d.getAdresseMada() != null ? d.getAdresseMada() : "");
            item.put("passeportNumero", d.getPasseportNumero() != null ? d.getPasseportNumero() : "");
            item.put("passeportId", d.getPasseportId() != null ? d.getPasseportId() : "");
            item.put("passeportDelivrance", d.getPasseportDelivrance() != null ? d.getPasseportDelivrance().toString() : "");
            item.put("passeportExpiration", d.getPasseportExpiration() != null ? d.getPasseportExpiration().toString() : "");
            item.put("demandeNumero", d.getDemandeNumero() != null ? d.getDemandeNumero() : "");
            item.put("typeDemandeId", d.getTypeDemandeId());
            item.put("typeVisaId", d.getTypeVisaId());
            item.put("visaTransformableNumero", d.getVisaTransformableNumero() != null ? d.getVisaTransformableNumero() : "");
            item.put("vtDateEntree", d.getVtDateEntree() != null ? d.getVtDateEntree().toString() : "");
            item.put("vtLieuEntree", d.getVtLieuEntree() != null ? d.getVtLieuEntree() : "");
            item.put("vtDateFin", d.getVtDateFin() != null ? d.getVtDateFin().toString() : "");
            item.put("piecesFourniesIds", pieceFournieService.findPresentPieceIds(d.getDemandeId()));
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVÉS — Construction de stubs (non persistés) depuis le formulaire
    // ─────────────────────────────────────────────────────────────────────────

    private Demandeur construireDemandeur(DemandeForm form) {
        Demandeur d = new Demandeur();
        if (form.getDemandeurId() != null) d.setId(form.getDemandeurId());
        d.setNom(form.getNom());
        d.setPrenoms(form.getPrenoms());
        d.setNomJeuneFille(form.getNomJeuneFille());
        d.setDateNaissance(form.getDateNaissance());
        d.setProfession(form.getProfession());
        d.setAdresseMada(form.getAdresseMada());
        d.setContactMada(form.getContactMada());
        if (form.getNationaliteId() != null)
            d.setNationalite(nomenclatureService.findNationalite(form.getNationaliteId()));
        if (form.getSituationFamilialeId() != null)
            d.setSituationFamiliale(nomenclatureService.findSituationFamiliale(form.getSituationFamilialeId()));
        return d;
    }

    private Passeport construirePasseport(DemandeForm form) {
        Passeport p = new Passeport();
        p.setNumero(form.getNumeroPasseport());
        p.setDateDelivrance(form.getDateDelivrance());
        p.setDateExpiration(form.getDateExpiration());
        return p;
    }

    private VisaTransformable construireVT(DemandeForm form) {
        String num = form.getVisaTransformableNumero();
        if (num == null || num.isBlank()) return null;
        VisaTransformable vt = new VisaTransformable();
        vt.setNumero(num.trim());
        vt.setDateEntree(form.getVisaTransformableDateEntree());
        vt.setLieuEntree(form.getVisaTransformableLieuEntree());
        vt.setDateFinVisa(form.getVisaTransformableDateFinVisa());
        return vt;
    }

    private Visa construireVisaInjecte(DemandeForm form) {
        if (form.getNumeroVisa() == null || form.getDateDebutVisa() == null || form.getDateFinVisa() == null)
            return null;
        Visa visa = new Visa();
        visa.setNumero(form.getNumeroVisa());
        visa.setDateDebut(form.getDateDebutVisa());
        visa.setDateFin(form.getDateFinVisa());
        return visa;
    }

    private CarteResident construireCarteInjectee(DemandeForm form) {
        if (form.getNumeroCarteResident() == null || form.getDateDebutCarte() == null || form.getDateFinCarte() == null)
            return null;
        CarteResident carte = new CarteResident();
        carte.setNumero(form.getNumeroCarteResident());
        carte.setDateDebut(form.getDateDebutCarte());
        carte.setDateFin(form.getDateFinCarte());
        return carte;
    }

    private void chargerDonneesUpload(Integer demandeId, Model model) {
        List<PieceFournie> piecesFournies = pieceFournieService.findAllForDemande(demandeId);
        model.addAttribute("piecesFournies", piecesFournies);
        Map<Integer, List<?>> fichiersByPiece = new LinkedHashMap<>();
        for (PieceFournie pf : piecesFournies) {
            fichiersByPiece.put(pf.getId(), pieceFichierService.findByPieceFournieId(pf.getId()));
        }
        model.addAttribute("fichiersByPiece", fichiersByPiece);
    }

    private void uploadFichiersCreation(Integer demandeId,
                                        Map<String, MultipartFile> fileMap,
                                        RedirectAttributes redirectAttrs) {
        if (fileMap == null || demandeId == null) return;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            String key = entry.getKey();
            MultipartFile file = entry.getValue();
            if (!key.startsWith("fichier_") || file == null || file.isEmpty()) continue;
            try {
                Integer pieceJustificativeId = Integer.parseInt(key.substring("fichier_".length()));
                pieceFournieService.findByDemandeIdAndPieceJustificativeId(demandeId, pieceJustificativeId)
                        .ifPresent(pf -> pieceFichierService.upload(pf.getId(), file));
            } catch (NumberFormatException ignored) {
            } catch (ResponseStatusException e) {
                redirectAttrs.addFlashAttribute("errorMessage",
                        "Erreur upload « " + file.getOriginalFilename() + " » : " + e.getReason());
            }
        }
    }

    private void chargerNomenclatures(Model model) {
        model.addAttribute("typesVisa", nomenclatureService.getTypesVisa());
        model.addAttribute("typesDemande", nomenclatureService.getTypesDemande());
        model.addAttribute("nationalites", nomenclatureService.getNationalites());
        model.addAttribute("situationsFamiliales", nomenclatureService.getSituationsFamiliales());
    }

    private DemandeForm buildFormFromDemande(Demande demande, Passeport passeport) {
        DemandeForm form = new DemandeForm();
        if (demande.getDemandeur() != null) {
            var d = demande.getDemandeur();
            form.setNom(d.getNom());
            form.setPrenoms(d.getPrenoms());
            form.setNomJeuneFille(d.getNomJeuneFille());
            form.setDateNaissance(d.getDateNaissance());
            form.setProfession(d.getProfession());
            form.setAdresseMada(d.getAdresseMada());
            form.setContactMada(d.getContactMada());
            if (d.getNationalite() != null) form.setNationaliteId(d.getNationalite().getId());
            if (d.getSituationFamiliale() != null) form.setSituationFamilialeId(d.getSituationFamiliale().getId());
        }
        if (passeport != null) {
            form.setNumeroPasseport(passeport.getNumero());
            form.setDateDelivrance(passeport.getDateDelivrance());
            form.setDateExpiration(passeport.getDateExpiration());
        }
        if (demande.getTypeVisa() != null) form.setTypeVisaId(demande.getTypeVisa().getId());
        if (demande.getTypeDemande() != null) form.setTypeDemandeId(demande.getTypeDemande().getId());
        if (demande.getVisaTransformable() != null) {
            var vt = demande.getVisaTransformable();
            form.setVisaTransformableNumero(vt.getNumero());
            form.setVisaTransformableDateEntree(vt.getDateEntree());
            form.setVisaTransformableLieuEntree(vt.getLieuEntree());
            form.setVisaTransformableDateFinVisa(vt.getDateFinVisa());
        }
        return form;
    }
}
