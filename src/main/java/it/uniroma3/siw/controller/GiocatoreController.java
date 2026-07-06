package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Giocatore;
import it.uniroma3.siw.service.GiocatoreService;
import it.uniroma3.siw.service.SquadraService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class GiocatoreController {

    private final GiocatoreService giocatoreService;
    private final SquadraService squadraService;

    public GiocatoreController(GiocatoreService giocatoreService, SquadraService squadraService) {
        this.giocatoreService = giocatoreService;
        this.squadraService = squadraService;
    }

    @GetMapping("/admin/squadre/{squadraId}/giocatori/nuovo")
    public String formNuovoGiocatore(@PathVariable Long squadraId, Model model) {
        Giocatore g = new Giocatore();
        g.setSquadra(squadraService.squadraById(squadraId));
        model.addAttribute("giocatore", g);
        return "admin-giocatore";
    }

    // ===== NUOVO: form di MODIFICA (riusa lo stesso template del "nuovo") =====
    @GetMapping("/admin/giocatori/{id}/modifica")
    public String formModificaGiocatore(@PathVariable Long id, Model model) {
        model.addAttribute("giocatore", giocatoreService.giocatoreById(id));
        return "admin-giocatore";
    }

    @PostMapping("/admin/giocatori")
    public String salvaGiocatore(@Valid @ModelAttribute("giocatore") Giocatore giocatore,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin-giocatore";
        }
        // id valorizzato dal campo hidden -> UPDATE, altrimenti INSERT
        giocatoreService.salvaGiocatore(giocatore);
        return "redirect:/squadre/" + giocatore.getSquadra().getId();
    }
}