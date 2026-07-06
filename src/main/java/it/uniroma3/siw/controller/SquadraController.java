package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Squadra;
import it.uniroma3.siw.service.SquadraService;
import it.uniroma3.siw.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SquadraController {

    private final SquadraService squadraService;
    private final TorneoService torneoService;

    public SquadraController(SquadraService squadraService, TorneoService torneoService) {
        this.squadraService = squadraService;
        this.torneoService = torneoService;
    }

    @GetMapping("/squadre")
    public String listaSquadre(Model model) {
        model.addAttribute("squadre", squadraService.listaSquadre());
        return "squadre";
    }

    @GetMapping("/squadre/{id}")
    public String dettaglioSquadra(@PathVariable Long id, Model model) {
        model.addAttribute("squadra", squadraService.squadraById(id));
        return "squadra";
    }

    // ===== ADMIN =====
    @GetMapping("/admin/squadre/nuova")
    public String formNuovaSquadra(Model model) {
        model.addAttribute("squadra", new Squadra());
        model.addAttribute("tornei", torneoService.listaTornei());
        return "admin-squadra";
    }

    // ===== NUOVO: form di MODIFICA (riusa lo stesso template del "nuova") =====
    @GetMapping("/admin/squadre/{id}/modifica")
    public String formModificaSquadra(@PathVariable Long id, Model model) {
        model.addAttribute("squadra", squadraService.squadraById(id));
        model.addAttribute("tornei", torneoService.listaTornei());
        return "admin-squadra";
    }

    @PostMapping("/admin/squadre")
    public String salvaSquadra(@Valid @ModelAttribute("squadra") Squadra squadra,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tornei", torneoService.listaTornei());
            return "admin-squadra";
        }
        // id valorizzato dal campo hidden -> UPDATE, altrimenti INSERT
        Squadra salvata = squadraService.salvaSquadra(squadra);
        return "redirect:/squadre/" + salvata.getId();
    }

    @PostMapping("/admin/squadre/{id}/elimina")
    public String eliminaSquadra(@PathVariable Long id) {
        squadraService.eliminaSquadra(id);
        return "redirect:/squadre";
    }
}