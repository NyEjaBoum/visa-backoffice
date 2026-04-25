package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.TypeVisa;
import com.visa.visa_backoffice.service.TypeVisaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/types-visa")
public class TypeVisaController extends ReferentielControllerSupport {

    private final TypeVisaService typeVisaService;

    public TypeVisaController(TypeVisaService typeVisaService) {
        this.typeVisaService = typeVisaService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("items", typeVisaService.findAll());
        setupListModel(model, "Types de visa", "Liste des types de visa", "/types-visa", true);
        return "referentiels/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("item", typeVisaService.findById(id));
        setupListModel(model, "Type de visa", "Détail", "/types-visa", true);
        return "referentiels/detail";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("item", new TypeVisa());
        setupFormModel(model, "Nouveau type de visa", "/types-visa", true, "Créer", "/types-visa");
        return "referentiels/formulaire";
    }

    @PostMapping
    public String creer(@ModelAttribute("item") TypeVisa item,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            typeVisaService.create(item);
            redirectAttrs.addFlashAttribute("successMessage", "Type de visa créé.");
            return "redirect:/types-visa";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Nouveau type de visa", "/types-visa", true, "Créer", "/types-visa");
            return "referentiels/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        model.addAttribute("item", typeVisaService.findById(id));
        setupFormModel(model, "Modifier type de visa", "/types-visa", true, "Enregistrer", "/types-visa/" + id + "/modifier");
        return "referentiels/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("item") TypeVisa item,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            typeVisaService.update(id, item);
            redirectAttrs.addFlashAttribute("successMessage", "Type de visa mis à jour.");
            return "redirect:/types-visa/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Modifier type de visa", "/types-visa", true, "Enregistrer", "/types-visa/" + id + "/modifier");
            return "referentiels/formulaire";
        }
    }
}
