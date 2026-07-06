package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Squadra;
import it.uniroma3.siw.model.Torneo;
import it.uniroma3.siw.repository.PartitaRepository;
import it.uniroma3.siw.repository.SquadraRepository;
import it.uniroma3.siw.repository.TorneoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SquadraService {

    private final SquadraRepository squadraRepo;
    private final TorneoRepository torneoRepo;
    private final PartitaRepository partitaRepo;

    public SquadraService(SquadraRepository squadraRepo, TorneoRepository torneoRepo,
                          PartitaRepository partitaRepo) {
        this.squadraRepo = squadraRepo;
        this.torneoRepo = torneoRepo;
        this.partitaRepo = partitaRepo;
    }

    @Transactional(readOnly = true)
    public List<Squadra> listaSquadre() { return squadraRepo.findAll(); }

    @Transactional(readOnly = true)
    public Squadra squadraById(Long id) { return squadraRepo.findByIdConGiocatori(id).orElseThrow(); }

    @Transactional
    public Squadra salvaSquadra(Squadra s) {
        if (s.getId() == null) {
            Set<Torneo> selezionati = s.getTornei();
            s.setTornei(new HashSet<>());
            Squadra salvata = squadraRepo.save(s);
            aggiornaTornei(salvata, selezionati);
            return salvata;
        }

        // update: niente save() sull'oggetto del form (giocatori vuoti, la merge
        // li cancellerebbe): aggiorno solo i campi e sincronizzo i tornei
        Squadra esistente = squadraRepo.findById(s.getId()).orElseThrow();
        esistente.setNome(s.getNome());
        esistente.setCitta(s.getCitta());
        esistente.setAnnoFondazione(s.getAnnoFondazione());
        aggiornaTornei(esistente, s.getTornei());
        return esistente;
    }

    // allinea le iscrizioni ai tornei selezionati nel form
    // (si lavora sul lato owning, cioè Torneo.squadre)
    private void aggiornaTornei(Squadra squadra, Set<Torneo> selezionati) {
        Set<Long> idSelezionati = new HashSet<>();
        if (selezionati != null) {
            for (Torneo t : selezionati) {
                if (t != null) idSelezionati.add(t.getId());
            }
        }

        // tolgo dai tornei deselezionati
        for (Torneo t : new ArrayList<>(squadra.getTornei())) {
            if (!idSelezionati.contains(t.getId())) {
                Torneo gestito = torneoRepo.findByIdJoinFetch(t.getId()).orElseThrow();
                gestito.getSquadre().remove(squadra);
                squadra.getTornei().remove(gestito);
            }
        }

        // aggiungo ai tornei selezionati
        for (Long idTorneo : idSelezionati) {
            Torneo gestito = torneoRepo.findByIdJoinFetch(idTorneo).orElseThrow();
            gestito.getSquadre().add(squadra);
            squadra.getTornei().add(gestito);
        }
    }

    // prima di eliminare la squadra vanno tolti i riferimenti, se no il DB
    // blocca per le foreign key
    @Transactional
    public void eliminaSquadra(Long id) {
        Squadra squadra = squadraRepo.findById(id).orElseThrow();

        // partite in casa o in trasferta
        partitaRepo.deleteAll(partitaRepo.findBySquadra(id));

        // tolgo la squadra dai tornei (lato owning della many-to-many)
        for (Torneo t : squadra.getTornei()) {
            t.getSquadre().remove(squadra);
        }

        squadraRepo.delete(squadra); // giocatori eliminati in cascata
    }
}
