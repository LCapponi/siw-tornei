package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Partita;
import it.uniroma3.siw.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PartitaController {

    private final PartitaService partitaService;
    private final TorneoService torneoService;
    private final SquadraService squadraService;
    private final ArbitroService arbitroService;
    private final CommentoService commentoService;
    private final CredentialsService credentialsService;

    public PartitaController(PartitaService partitaService, TorneoService torneoService,
                             SquadraService squadraService, ArbitroService arbitroService,
                             CommentoService commentoService, CredentialsService credentialsService) {
        this.partitaService = partitaService;
        this.torneoService = torneoService;
        this.squadraService = squadraService;
        this.arbitroService = arbitroService;
        this.commentoService = commentoService;
        this.credentialsService = credentialsService;
    }

    @GetMapping("/partite/{id}")
    public String dettaglioPartita(@PathVariable Long id, Model model,
                                   @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("partita", partitaService.partitaById(id));
        model.addAttribute("commenti", commentoService.commentiPartita(id));
        model.addAttribute("currentUserId", currentUserId(user));
        return "partita";
    }

    // id dell'utente di dominio loggato (null se anonimo): serve per mostrare
    // il form di modifica solo all'autore del commento
    private Long currentUserId(UserDetails user) {
        if (user == null) return null;
        Credentials c = credentialsService.getCredentials(user.getUsername());
        return (c != null && c.getUser() != null) ? c.getUser().getId() : null;
    }

    // ===== ADMIN =====
    @GetMapping("/admin/partite/nuova")
    public String formNuovaPartita(Model model) {
        model.addAttribute("partita", new Partita());
        model.addAttribute("tornei", torneoService.listaTornei());
        model.addAttribute("squadre", squadraService.listaSquadre());
        model.addAttribute("arbitri", arbitroService.listaArbitri());
        return "admin-partita";
    }

    @PostMapping("/admin/partite")
    public String salvaPartita(@Valid @ModelAttribute("partita") Partita partita,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tornei", torneoService.listaTornei());
            model.addAttribute("squadre", squadraService.listaSquadre());
            model.addAttribute("arbitri", arbitroService.listaArbitri());
            return "admin-partita";
        }
        Partita salvata = partitaService.salvaPartita(partita);
        return "redirect:/partite/" + salvata.getId();
    }

    @PostMapping("/admin/partite/{id}/risultato")
    public String registraRisultato(@PathVariable Long id,
                                    @RequestParam int goalsHome,
                                    @RequestParam int goalsAway) {
        partitaService.registraRisultato(id, goalsHome, goalsAway);
        return "redirect:/partite/" + id;
    }

    @PostMapping("/admin/partite/{id}/elimina")
    public String eliminaPartita(@PathVariable Long id, @RequestParam Long torneoId) {
        partitaService.eliminaPartita(id);
        return "redirect:/tornei/" + torneoId;
    }
}
