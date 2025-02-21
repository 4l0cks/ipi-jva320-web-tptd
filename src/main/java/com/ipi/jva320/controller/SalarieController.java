package com.ipi.jva320.controller;

import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.service.SalarieAideADomicileService;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/salaries")
public class SalarieController {

  @Autowired
  private SalarieAideADomicileService service;

  @GetMapping("/{id}")
  public String getSalarie(@PathVariable("id") Long id,
      Model model) {
    try {
      SalarieAideADomicile salarie = service.getSalarie(id);
    model.addAttribute("salarie", salarie);
    model.addAttribute("isEdit", true);
    return "detail_Salarie";
  } catch (EntityNotFoundException e) {
      model.addAttribute("erreur", "Salarie avec ID " + id + " non trouvé");
      return "detail_salarie";
      }
  }

  @GetMapping("/detail")
  public String showDetailSalarie(Model model) {
    SalarieAideADomicile aide = new SalarieAideADomicile(
        "Jeannette Dupontelle",
        LocalDate.of(2021, 7, 1),
        LocalDate.now(),
        0, 0, 10, 1, 0
    );
    model.addAttribute("salarie", aide);
    return "detail_Salarie";
  }

  @PostMapping("/save")
  public String createSalarie(@ModelAttribute SalarieAideADomicile salarie, Model model){
    try {
      service.creerSalarieAideADomicile(salarie);
      return "redirect:/salaries";
    } catch (SalarieException |EntityExistsException e) {
      model.addAttribute("erreur", e.getMessage());
      return "new_salarie";
    }
  }

  @GetMapping("/aide/new")
  public String afficherFormulaireCreation(Model model) {
    model.addAttribute("salarie", new SalarieAideADomicile("", LocalDate.now(), LocalDate.now(), 0, 0, 0, 0, 0));
    return "new_salarie";
  }

  @GetMapping
  public String afficherListeSalaries(Model model) {
    List<SalarieAideADomicile> salaries = service.getSalaries();
    model.addAttribute("salaries", salaries);
    return "list";
  }

  @GetMapping("/{id}/edit")
  public String showEditForm(@PathVariable("id") Long id, Model model) {
    try {
      SalarieAideADomicile salarie = service.getSalarie(id);
      model.addAttribute("salarie", salarie);
      model.addAttribute("isEdit", true);
      return "detail_Salarie";
    } catch (EntityNotFoundException e) {
      model.addAttribute("erreur", "Le salarié avec ID " + id + " n'a pas été trouvé.");
      return "detail_Salarie";
    }
  }

  @PostMapping("/{id}")
  public String updateSalarie(@PathVariable("id") Long id,
      @ModelAttribute SalarieAideADomicile salarie,
      Model model) {
    try {
      salarie.setId(id);
      service.updateSalarieAideADomicile(salarie);
      return "redirect:/salaries";
    } catch (SalarieException e) {
      model.addAttribute("erreur", e.getMessage());
      return "detail_Salarie";
    }
  }

  @GetMapping("/{id}/delete")
  public String deleteSalarie(@PathVariable("id") Long id, Model model) {
    try {
      service.deleteSalarieAideADomicile(id);
      return "redirect:/salaries";
    } catch (SalarieException e) {
      model.addAttribute("erreur", e.getMessage());
      return "list";
    }
  }

  @GetMapping("/search")
  public String afficherListeSalariesByNom(@RequestParam(value = "nom", required = false) String nom, Model model) {
    List<SalarieAideADomicile> salaries;

    if (nom != null && !nom.isEmpty()) {
      salaries = service.getSalaries(nom);
    } else {
      salaries = service.getSalaries();
    }
    if (salaries.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun salarié trouvé avec ce nom");
    }

    model.addAttribute("salaries", salaries);
    model.addAttribute("nom", nom);
    return "list";
  }
}

