package it.uniroma3.siw.service;

import it.uniroma3.siw.exception.DuplicateTorneoException;
import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.PartitaRepository;
import it.uniroma3.siw.repository.TorneoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TorneoService {

    private final TorneoRepository torneoRepo;
    private final PartitaRepository partitaRepo;

    public TorneoService(TorneoRepository torneoRepo, PartitaRepository partitaRepo) {
        this.torneoRepo = torneoRepo;
        this.partitaRepo = partitaRepo;
    }

    @Transactional(readOnly = true)
    public List<Torneo> listaTornei() { return torneoRepo.findAll(); }

    @Transactional(readOnly = true)
    public Torneo torneoById(Long id) { return torneoRepo.findByIdJoinFetch(id).orElseThrow(); }

    @Transactional
    public Torneo salvaTorneo(Torneo t) {
        boolean duplicato = t.getId() == null
            ? torneoRepo.existsByNomeAndAnno(t.getNome(), t.getAnno())
            : torneoRepo.existsByNomeAndAnnoAndIdNot(t.getNome(), t.getAnno(), t.getId());
        if (duplicato) {
            throw new DuplicateTorneoException(t.getNome(), t.getAnno());
        }

        if (t.getId() == null) {
            return torneoRepo.save(t);
        }

        // update: niente save() sull'oggetto del form (ha le collezioni vuote,
        // la merge le cancellerebbe): aggiorno solo i campi, poi dirty checking
        Torneo esistente = torneoRepo.findById(t.getId()).orElseThrow();
        esistente.setNome(t.getNome());
        esistente.setAnno(t.getAnno());
        esistente.setDescrizione(t.getDescrizione());
        return esistente;
    }

    @Transactional
    public void eliminaTorneo(Long id) { torneoRepo.deleteById(id); }

    // ===== CLASSIFICA (caso d'uso chiave, multi-entità) =====
    @Transactional(readOnly = true)
    public List<ClassificaRow> classifica(Long torneoId) {
        Torneo t = torneoRepo.findByIdJoinFetch(torneoId).orElseThrow();

        Map<Long, ClassificaRow> mappa = new HashMap<>();
        for (Squadra s : t.getSquadre()) {
            mappa.put(s.getId(), new ClassificaRow(s.getNome()));
        }

        for (Partita p : partitaRepo.findGiocateByTorneo(torneoId)) {
            int gh = p.getGoalsHome() != null ? p.getGoalsHome() : 0;
            int ga = p.getGoalsAway() != null ? p.getGoalsAway() : 0;
            ClassificaRow rh = mappa.get(p.getSquadraHome().getId());
            ClassificaRow ra = mappa.get(p.getSquadraAway().getId());
            if (rh != null) rh.aggiungiRisultato(gh, ga);
            if (ra != null) ra.aggiungiRisultato(ga, gh);
        }

        List<ClassificaRow> ris = new ArrayList<>(mappa.values());
        ris.sort(Comparator
                .comparingInt(ClassificaRow::getPunti).reversed()
                .thenComparing(Comparator.comparingInt(ClassificaRow::getDifferenzaReti).reversed())
                .thenComparing(Comparator.comparingInt(ClassificaRow::getGolFatti).reversed()));
        return ris;
    }
}
