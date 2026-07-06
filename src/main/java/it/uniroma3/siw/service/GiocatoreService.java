package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Giocatore;
import it.uniroma3.siw.repository.GiocatoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GiocatoreService {

    private final GiocatoreRepository giocatoreRepo;

    public GiocatoreService(GiocatoreRepository giocatoreRepo) {
        this.giocatoreRepo = giocatoreRepo;
    }

    @Transactional(readOnly = true)
    public Giocatore giocatoreById(Long id) { return giocatoreRepo.findById(id).orElseThrow(); }

    @Transactional
    public Giocatore salvaGiocatore(Giocatore g) { return giocatoreRepo.save(g); }
}
