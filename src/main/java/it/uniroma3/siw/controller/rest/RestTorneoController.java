package it.uniroma3.siw.controller.rest;

import it.uniroma3.siw.model.Torneo;
import it.uniroma3.siw.service.ClassificaRow;
import it.uniroma3.siw.service.TorneoService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // Permette l'accesso dal dev server di React/Vite
@RestController
@RequestMapping("rest/tornei")
public class RestTorneoController {

    private final TorneoService torneoService;

    public RestTorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    // GET /rest/tornei  ->  elenco tornei (JSON)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Torneo> list() {
        return torneoService.listaTornei();
    }


    @GetMapping(value = "/{id}/classifica", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ClassificaRow> classifica(@PathVariable Long id) {
        return torneoService.classifica(id);
    }
}