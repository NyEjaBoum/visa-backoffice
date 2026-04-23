package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.model.Nationalite;
import com.visa.visa_backoffice.service.NationaliteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nationalites")
public class NationaliteController extends ReferentielControllerSupport {

    private final NationaliteService nationaliteService;

    public NationaliteController(NationaliteService nationaliteService) {
        this.nationaliteService = nationaliteService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("items", nationaliteService.findAll());
        setupListModel(model, "Nationalités", "Liste des nationalités", "/nationalites", false);
        return "referentiels/liste";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("item", nationaliteService.findById(id));
        setupListModel(model, "Nationalité", "Détail", "/nationalites", false);
        return "referentiels/detail";
    }

    @GetMapping("/nouveau")
    public String nouveauForm(Model model) {
        model.addAttribute("item", new Nationalite());
        setupFormModel(model, "Nouvelle nationalité", "/nationalites", false, "Créer", "/nationalites");
        return "referentiels/formulaire";
    }

    @PostMapping
    public String creer(@ModelAttribute("item") Nationalite item,
                        RedirectAttributes redirectAttrs,
                        Model model) {
        try {
            nationaliteService.create(item);
            redirectAttrs.addFlashAttribute("successMessage", "Nationalité créée.");
            return "redirect:/nationalites";
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Nouvelle nationalité", "/nationalites", false, "Créer", "/nationalites");
            return "referentiels/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String modifierForm(@PathVariable Integer id, Model model) {
        model.addAttribute("item", nationaliteService.findById(id));
        setupFormModel(model, "Modifier nationalité", "/nationalites", false, "Enregistrer", "/nationalites/" + id + "/modifier");
        return "referentiels/formulaire";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(@PathVariable Integer id,
                           @ModelAttribute("item") Nationalite item,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        try {
            nationaliteService.update(id, item);
            redirectAttrs.addFlashAttribute("successMessage", "Nationalité mise à jour.");
            return "redirect:/nationalites/" + id;
        } catch (ResponseStatusException e) {
            model.addAttribute("errorMessage", e.getReason());
            setupFormModel(model, "Modifier nationalité", "/nationalites", false, "Enregistrer", "/nationalites/" + id + "/modifier");
            return "referentiels/formulaire";
        }
    }
}
