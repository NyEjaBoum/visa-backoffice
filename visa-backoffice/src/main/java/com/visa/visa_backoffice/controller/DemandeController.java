package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.DemandeForm;
import com.visa.visa_backoffice.model.DemandeVisa;
import com.visa.visa_backoffice.service.DemandeVisaService;
import com.visa.visa_backoffice.service.NomenclatureService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeVisaService demandeVisaService;
    private final NomenclatureService nomenclatureService;

    public DemandeController(DemandeVisaService demandeVisaService,
                             NomenclatureService nomenclatureService) {
        this.demandeVisaService = demandeVisaService;
        this.nomenclatureService = nomenclatureService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("demandes", demandeVisaService.findAll());
        return "demandes/liste";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("form", new DemandeForm());
        chargerNomenclatures(model);
        return "demandes/formulaire";
    }

    @PostMapping
    public String creer(@ModelAttribute("form") DemandeForm form,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            form.validateOrThrow();
            DemandeVisa demande = demandeVisaService.creer(form);
            redirectAttrs.addFlashAttribute("successMessage",
                    "Demande créée avec succès. Numéro : " + demande.getNumDemande());
            return "redirect:/demandes/" + demande.getId();
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            chargerNomenclatures(model);
            return "demandes/formulaire";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        DemandeVisa demande = demandeVisaService.findByIdOrThrow(id);
        model.addAttribute("demande", demande);
        boolean modifiable = demande.getStatut() != null
                && "CREE".equalsIgnoreCase(demande.getStatut().getLibelle());
        model.addAttribute("modifiable", modifiable);
        return "demandes/detail";
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        DemandeVisa demande = demandeVisaService.findByIdOrThrow(id);

        if (demande.getStatut() == null || !"CREE".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            return "redirect:/demandes/" + id;
        }

        DemandeForm form = buildFormFromDemande(demande);
        model.addAttribute("form", form);
        model.addAttribute("demandeId", id);
        model.addAttribute("numDemande", demande.getNumDemande());
        chargerNomenclatures(model);
        return "demandes/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("form") DemandeForm form,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            form.validateOrThrow();
            demandeVisaService.modifier(id, form);
            redirectAttrs.addFlashAttribute("successMessage", "Demande mise à jour avec succès.");
            return "redirect:/demandes/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            model.addAttribute("demandeId", id);
            chargerNomenclatures(model);
            return "demandes/formulaire";
        }
    }

    private void chargerNomenclatures(Model model) {
        model.addAttribute("typesVisa", nomenclatureService.getTypesVisa());
        model.addAttribute("typesDemande", nomenclatureService.getTypesDemande());
        model.addAttribute("nationalites", nomenclatureService.getNationalites());
        model.addAttribute("situationsFamiliales", nomenclatureService.getSituationsFamiliales());
    }

    private DemandeForm buildFormFromDemande(DemandeVisa demande) {
        DemandeForm form = new DemandeForm();
        if (demande.getIndividu() != null) {
            var ind = demande.getIndividu();
            form.setNom(ind.getNom());
            form.setPrenoms(ind.getPrenoms());
            form.setNomJeuneFille(ind.getNomJeuneFille());
            form.setDateNaissance(ind.getDateNaissance());
            form.setProfession(ind.getProfession());
            form.setAdresseMada(ind.getAdresseMada());
            form.setContactMada(ind.getContactMada());
            if (ind.getNationalite() != null) form.setNationaliteId(ind.getNationalite().getId());
            if (ind.getSituationFamiliale() != null) form.setSituationFamilialeId(ind.getSituationFamiliale().getId());
        }
        if (demande.getPasseport() != null) {
            form.setNumeroPasseport(demande.getPasseport().getNumeroPass());
            form.setDateDelivrance(demande.getPasseport().getDateDelivrance());
            form.setDateExpiration(demande.getPasseport().getDateExpiration());
        }
        if (demande.getTypeVisa() != null) form.setTypeVisaId(demande.getTypeVisa().getId());
        if (demande.getTypeDemande() != null) form.setTypeDemandeId(demande.getTypeDemande().getId());
        if (demande.getVisaTransformable() != null) {
            var vt = demande.getVisaTransformable();
            form.setVisaTransformableReference(vt.getReference());
            form.setVisaTransformableDateEntree(vt.getDateEntree());
            form.setVisaTransformableLieuEntree(vt.getLieuEntree());
            form.setVisaTransformableDateFinVisa(vt.getDateFinVisa());
        }
        return form;
    }
}
