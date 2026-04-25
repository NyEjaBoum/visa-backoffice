package com.visa.visa_backoffice.controller;

import com.visa.visa_backoffice.dto.LoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("successMessage", "Connexion réussie");
        return "redirect:/demandes";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("successMessage", "Déconnexion réussie");
        return "redirect:/login";
    }
}
