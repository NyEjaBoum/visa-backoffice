package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.DemandeForm;
import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.DossierComplet;
import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    public DemandeController(DemandeService demandeService,
                             NomenclatureService nomenclatureService,
                             PasseportService passeportService,
                             PieceFournieService pieceFournieService,
                             PieceJustificativeService pieceJustificativeService) {
        this.demandeService = demandeService;
        this.nomenclatureService = nomenclatureService;
        this.passeportService = passeportService;
        this.pieceFournieService = pieceFournieService;
        this.pieceJustificativeService = pieceJustificativeService;
    }

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

    @PostMapping
    public String creer(@ModelAttribute("form") DemandeForm form,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            form.validateOrThrow();
            Demande demande = demandeService.creer(form);
            redirectAttrs.addFlashAttribute("successMessage", "Demande créée avec succès. Numéro : " + demande.getNumDemande());
            return "redirect:/demandes/" + demande.getId();
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            chargerNomenclatures(model);
            model.addAttribute("piecesJustificatives", pieceJustificativeService.findAll());
            return "demandes/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Demande demande = demandeService.findByIdOrThrow(id);
        model.addAttribute("demande", demande);
        Passeport passeport = passeportService.findLastForDemandeur(
            demande.getDemandeur() == null ? null : demande.getDemandeur().getId()
        ).orElse(null);
        model.addAttribute("passeport", passeport);
        model.addAttribute("piecesFournies", pieceFournieService.findAllForDemande(id));

        boolean modifiable = demande.getStatut() != null && "CREE".equalsIgnoreCase(demande.getStatut().getLibelle());
        model.addAttribute("modifiable", modifiable);
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
        
        return "demandes/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("form") DemandeForm form,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            form.validateOrThrow();
            demandeService.modifier(id, form);
            redirectAttrs.addFlashAttribute("successMessage", "Demande mise à jour avec succès.");
            return "redirect:/demandes/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            model.addAttribute("demandeId", id);
            chargerNomenclatures(model);
            model.addAttribute("piecesJustificatives", pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()));
            return "demandes/formulaire";
        }
    }

    // --- Gardé pour l'autocomplete (Recherche) ---
    @GetMapping("/antecedents/autocomplete")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> autocomplete(
            @RequestParam(required = false, defaultValue = "") String q) {
        if (q.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
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