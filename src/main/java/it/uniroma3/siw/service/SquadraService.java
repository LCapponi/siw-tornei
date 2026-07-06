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
        // INSERT: salvo la squadra e poi registro le iscrizioni ai tornei scelti
        if (s.getId() == null) {
            Set<Torneo> selezionati = s.getTornei();
            s.setTornei(new HashSet<>());
            Squadra salvata = squadraRepo.save(s);
            aggiornaTornei(salvata, selezionati);
            return salvata;
        }

        // UPDATE: il form invia solo i campi scalari e il multi-select dei tornei,
        // quindi la Squadra ricevuta ha giocatori VUOTI. Un save() farebbe una merge
        // che copierebbe anche la collezione vuota, eliminando tutti i giocatori
        // (cascade + orphanRemoval). Carico quindi l'entità gestita, aggiorno solo
        // i campi del form e sincronizzo le iscrizioni ai tornei.
        Squadra esistente = squadraRepo.findById(s.getId()).orElseThrow();
        esistente.setNome(s.getNome());
        esistente.setCitta(s.getCitta());
        esistente.setAnnoFondazione(s.getAnnoFondazione());
        aggiornaTornei(esistente, s.getTornei());
        return esistente;
    }

    // Allinea le iscrizioni della squadra ai tornei selezionati nel form.
    // Si agisce sempre sul lato owning della many-to-many (Torneo.squadre),
    // perché è la sua collezione a governare la join table torneo_squadra.
    private void aggiornaTornei(Squadra squadra, Set<Torneo> selezionati) {
        Set<Long> idSelezionati = new HashSet<>();
        if (selezionati != null) {
            for (Torneo t : selezionati) {
                if (t != null) idSelezionati.add(t.getId());
            }
        }

        // 1) rimuovo la squadra dai tornei deselezionati
        for (Torneo t : new ArrayList<>(squadra.getTornei())) {
            if (!idSelezionati.contains(t.getId())) {
                Torneo gestito = torneoRepo.findByIdJoinFetch(t.getId()).orElseThrow();
                gestito.getSquadre().remove(squadra);
                squadra.getTornei().remove(gestito);
            }
        }

        // 2) aggiungo la squadra ai tornei selezionati (Set: nessun duplicato)
        for (Long idTorneo : idSelezionati) {
            Torneo gestito = torneoRepo.findByIdJoinFetch(idTorneo).orElseThrow();
            gestito.getSquadre().add(squadra);
            squadra.getTornei().add(gestito);
        }
    }

    // Caso d'uso admin "eliminazione di squadre": il service coordina più repository.
    // Non basta deleteById: prima vanno rimossi i riferimenti alla squadra
    // (partite giocate/programmate e iscrizioni ai tornei), altrimenti il DB
    // solleva una violazione di chiave esterna.
    @Transactional
    public void eliminaSquadra(Long id) {
        Squadra squadra = squadraRepo.findById(id).orElseThrow();

        // 1) elimino le partite in cui la squadra è coinvolta (FK squadraHome/squadraAway)
        partitaRepo.deleteAll(partitaRepo.findBySquadra(id));

        // 2) tolgo la squadra dai tornei: Torneo è il lato owning della many-to-many,
        //    quindi è la sua collezione a governare la join table torneo_squadra
        for (Torneo t : squadra.getTornei()) {
            t.getSquadre().remove(squadra);
        }

        // 3) elimino la squadra: i giocatori vengono rimossi in cascata 
        squadraRepo.delete(squadra);
    }
}
