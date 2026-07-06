package it.uniroma3.siw.controller;

import it.uniroma3.siw.exception.DuplicateTorneoException;
import it.uniroma3.siw.model.Torneo;
import it.uniroma3.siw.service.PartitaService;
import it.uniroma3.siw.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class TorneoController {

    private final TorneoService torneoService;
    private final PartitaService partitaService;

    public TorneoController(TorneoService torneoService, PartitaService partitaService) {
        this.torneoService = torneoService;
        this.partitaService = partitaService;
    }

    @GetMapping("/tornei")
    public String listaTornei(Model model) {
        model.addAttribute("tornei", torneoService.listaTornei());
        return "tornei";
    }

    @GetMapping("/tornei/{id}")
    public String dettaglioTorneo(@PathVariable Long id, Model model) {
        model.addAttribute("torneo", torneoService.torneoById(id));
        model.addAttribute("partite", partitaService.partiteByTorneo(id));
        return "torneo";
    }

    @GetMapping("/tornei/{id}/classifica")
    public String classifica(@PathVariable Long id, Model model) {
        model.addAttribute("torneo", torneoService.torneoById(id));
        model.addAttribute("classifica", torneoService.classifica(id)); // <-- passa la lista al template
        return "classifica";
    }

    // ===== ADMIN =====
    @GetMapping("/admin/tornei/nuovo")
    public String formNuovoTorneo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "admin-torneo";
    }

    // ===== NUOVO: form di MODIFICA (riusa lo stesso template del "nuovo") =====
    @GetMapping("/admin/tornei/{id}/modifica")
    public String formModificaTorneo(@PathVariable Long id, Model model) {
        model.addAttribute("torneo", torneoService.torneoById(id));
        return "admin-torneo";
    }

    @PostMapping("/admin/tornei")
    public String salvaTorneo(@Valid @ModelAttribute("torneo") Torneo torneo,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin-torneo";
        }
        try {
            // Se il torneo arriva con id valorizzato (dal campo hidden) -> UPDATE, altrimenti INSERT
            Torneo salvato = torneoService.salvaTorneo(torneo);
            return "redirect:/tornei/" + salvato.getId();
        } catch (DuplicateTorneoException e) {
            bindingResult.reject("torneo.duplicate", e.getMessage());
            return "admin-torneo";
        }
    }

    @PostMapping("/admin/tornei/{id}/elimina")
    public String eliminaTorneo(@PathVariable Long id) {
        torneoService.eliminaTorneo(id);
        return "redirect:/tornei";
    }
}