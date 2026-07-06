package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Partita;
import it.uniroma3.siw.repository.PartitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartitaService {

    private final PartitaRepository partitaRepo;

    public PartitaService(PartitaRepository partitaRepo) {
        this.partitaRepo = partitaRepo;
    }

    @Transactional(readOnly = true)
    public List<Partita> partiteByTorneo(Long torneoId) { return partitaRepo.findByTorneo(torneoId); }

    @Transactional(readOnly = true)
    public Partita partitaById(Long id) { return partitaRepo.findByIdCompleta(id).orElseThrow(); }

    @Transactional
    public Partita salvaPartita(Partita p) {
        if (p.getStato() == null) p.setStato("SCHEDULED");
        return partitaRepo.save(p);
    }

    @Transactional
    public void registraRisultato(Long partitaId, int gh, int ga) {
        Partita p = partitaRepo.findById(partitaId).orElseThrow();
        p.setGoalsHome(gh);
        p.setGoalsAway(ga);
        p.setStato("PLAYED");
        // salvataggio automatico al commit (dirty checking)
    }

    @Transactional
    public void eliminaPartita(Long id) { partitaRepo.deleteById(id); }
}
