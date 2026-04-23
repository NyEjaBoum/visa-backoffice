package com.visa.visa_backoffice.controller;

import org.springframework.ui.Model;

public abstract class ReferentielControllerSupport {

    protected void setupListModel(Model model,
                                 String title,
                                 String subtitle,
                                 String basePath,
                                 boolean idEditable) {
        model.addAttribute("title", title);
        model.addAttribute("subtitle", subtitle);
        model.addAttribute("basePath", basePath);
        model.addAttribute("idEditable", idEditable);
    }

    protected void setupFormModel(Model model,
                                 String title,
                                 String basePath,
                                 boolean idEditable,
                                 String submitLabel,
                                 String formAction) {
        model.addAttribute("title", title);
        model.addAttribute("basePath", basePath);
        model.addAttribute("idEditable", idEditable);
        model.addAttribute("submitLabel", submitLabel);
        model.addAttribute("formAction", formAction);
    }
}
