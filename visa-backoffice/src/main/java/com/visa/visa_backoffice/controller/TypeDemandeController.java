package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.TypeDemande;
import com.visa.visa_backoffice.service.TypeDemandeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/types-demande")
public class TypeDemandeController extends ReferentielControllerSupport {

    private final TypeDemandeService typeDemandeService;

    public TypeDemandeController(TypeDemandeService typeDemandeService) {
        this.typeDemandeService = typeDemandeService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("items", typeDemandeService.findAll());
        setupListModel(model, "Types de demande", "Liste des types de demande", "/types-demande", true);
        return "referentiels/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("item", typeDemandeService.findById(id));
        setupListModel(model, "Type de demande", "Détail", "/types-demande", true);
        return "referentiels/detail";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("item", new TypeDemande());
        setupFormModel(model, "Nouveau type de demande", "/types-demande", true, "Créer", "/types-demande");
        return "referentiels/formulaire";
    }

    @PostMapping
    public String creer(@ModelAttribute("item") TypeDemande item,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            typeDemandeService.create(item);
            redirectAttrs.addFlashAttribute("successMessage", "Type de demande créé.");
            return "redirect:/types-demande";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Nouveau type de demande", "/types-demande", true, "Créer", "/types-demande");
            return "referentiels/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        model.addAttribute("item", typeDemandeService.findById(id));
        setupFormModel(model, "Modifier type de demande", "/types-demande", true, "Enregistrer", "/types-demande/" + id + "/modifier");
        return "referentiels/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("item") TypeDemande item,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            typeDemandeService.update(id, item);
            redirectAttrs.addFlashAttribute("successMessage", "Type de demande mis à jour.");
            return "redirect:/types-demande/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Modifier type de demande", "/types-demande", true, "Enregistrer", "/types-demande/" + id + "/modifier");
            return "referentiels/formulaire";
        }
    }
}
