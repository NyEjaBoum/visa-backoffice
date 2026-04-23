package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.SituationFamiliale;
import com.visa.visa_backoffice.service.SituationFamilialeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/situations-familiales")
public class SituationFamilialeController extends ReferentielControllerSupport {

    private final SituationFamilialeService situationFamilialeService;

    public SituationFamilialeController(SituationFamilialeService situationFamilialeService) {
        this.situationFamilialeService = situationFamilialeService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("items", situationFamilialeService.findAll());
        setupListModel(model, "Situations familiales", "Liste des situations familiales", "/situations-familiales", true);
        return "referentiels/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("item", situationFamilialeService.findById(id));
        setupListModel(model, "Situation familiale", "Détail", "/situations-familiales", true);
        return "referentiels/detail";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("item", new SituationFamiliale());
        setupFormModel(model, "Nouvelle situation familiale", "/situations-familiales", true, "Créer", "/situations-familiales");
        return "referentiels/formulaire";
    }

    @PostMapping
    public String creer(@ModelAttribute("item") SituationFamiliale item,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            situationFamilialeService.create(item);
            redirectAttrs.addFlashAttribute("successMessage", "Situation familiale créée.");
            return "redirect:/situations-familiales";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Nouvelle situation familiale", "/situations-familiales", true, "Créer", "/situations-familiales");
            return "referentiels/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        model.addAttribute("item", situationFamilialeService.findById(id));
        setupFormModel(model, "Modifier situation familiale", "/situations-familiales", true, "Enregistrer", "/situations-familiales/" + id + "/modifier");
        return "referentiels/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("item") SituationFamiliale item,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            situationFamilialeService.update(id, item);
            redirectAttrs.addFlashAttribute("successMessage", "Situation familiale mise à jour.");
            return "redirect:/situations-familiales/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Modifier situation familiale", "/situations-familiales", true, "Enregistrer", "/situations-familiales/" + id + "/modifier");
            return "referentiels/formulaire";
        }
    }
}
