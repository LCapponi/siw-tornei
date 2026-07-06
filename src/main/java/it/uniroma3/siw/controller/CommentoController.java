package it.uniroma3.siw.controller;

import it.uniroma3.siw.service.CommentoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentoController {

    private final CommentoService commentoService;

    public CommentoController(CommentoService commentoService) {
        this.commentoService = commentoService;
    }

    @PostMapping("/commenti/crea")
    public String creaCommento(@RequestParam Long partitaId,
                               @RequestParam String testo,
                               @AuthenticationPrincipal UserDetails user) {
        commentoService.creaCommento(partitaId, user.getUsername(), testo);
        return "redirect:/partite/" + partitaId;
    }

    @PostMapping("/commenti/{id}/modifica")
    public String modificaCommento(@PathVariable Long id,
                                   @RequestParam Long partitaId,
                                   @RequestParam String testo,
                                   @AuthenticationPrincipal UserDetails user) {
        commentoService.modificaCommento(id, user.getUsername(), testo);
        return "redirect:/partite/" + partitaId;
    }
}
