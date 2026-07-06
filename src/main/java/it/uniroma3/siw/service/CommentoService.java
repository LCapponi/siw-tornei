package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CommentoRepository;
import it.uniroma3.siw.repository.PartitaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentoService {

    private final CommentoRepository commentoRepo;
    private final PartitaRepository partitaRepo;
    private final CredentialsService credentialsService;

    public CommentoService(CommentoRepository commentoRepo,
                           PartitaRepository partitaRepo,
                           CredentialsService credentialsService) {
        this.commentoRepo = commentoRepo;
        this.partitaRepo = partitaRepo;
        this.credentialsService = credentialsService;
    }

    @Transactional(readOnly = true)
    public List<Commento> commentiPartita(Long partitaId) {
        return commentoRepo.findByPartita(partitaId);
    }

    @Transactional
    public Commento creaCommento(Long partitaId, String username, String testo) {
        Credentials credentials = credentialsService.getCredentials(username);
        Commento c = new Commento();
        c.setPartita(partitaRepo.findById(partitaId).orElseThrow());
        c.setUser(credentials.getUser());
        c.setTesto(testo);
        return commentoRepo.save(c);
    }

    @Transactional
    public void modificaCommento(Long commentoId, String username, String nuovoTesto) {
        Commento c = commentoRepo.findById(commentoId).orElseThrow();
        Credentials credentials = credentialsService.getCredentials(username);
        // un utente può modificare solo i propri commenti
        if (!c.getUser().equals(credentials.getUser())) {
            throw new AccessDeniedException("Non puoi modificare commenti altrui");
        }
        c.setTesto(nuovoTesto);
        // dirty checking
    }
}
