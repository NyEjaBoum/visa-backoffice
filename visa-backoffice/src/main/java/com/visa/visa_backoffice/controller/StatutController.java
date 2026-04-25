package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.Statut;
import com.visa.visa_backoffice.service.StatutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/statuts")
public class StatutController extends ReferentielControllerSupport {

    private final StatutService statutService;

    public StatutController(StatutService statutService) {
        this.statutService = statutService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("items", statutService.findAll());
        setupListModel(model, "Statuts", "Liste des statuts", "/statuts", true);
        return "referentiels/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("item", statutService.findById(id));
        setupListModel(model, "Statut", "Détail", "/statuts", true);
        return "referentiels/detail";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("item", new Statut());
        setupFormModel(model, "Nouveau statut", "/statuts", true, "Créer", "/statuts");
        return "referentiels/formulaire";
    }

    @PostMapping
    public String creer(@ModelAttribute("item") Statut item,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            statutService.create(item);
            redirectAttrs.addFlashAttribute("successMessage", "Statut créé.");
            return "redirect:/statuts";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Nouveau statut", "/statuts", true, "Créer", "/statuts");
            return "referentiels/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        model.addAttribute("item", statutService.findById(id));
        setupFormModel(model, "Modifier statut", "/statuts", true, "Enregistrer", "/statuts/" + id + "/modifier");
        return "referentiels/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("item") Statut item,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            statutService.update(id, item);
            redirectAttrs.addFlashAttribute("successMessage", "Statut mis à jour.");
            return "redirect:/statuts/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Modifier statut", "/statuts", true, "Enregistrer", "/statuts/" + id + "/modifier");
            return "referentiels/formulaire";
        }
    }
}
