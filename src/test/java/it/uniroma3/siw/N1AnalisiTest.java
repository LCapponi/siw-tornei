package it.uniroma3.siw;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ANALISI SPERIMENTALE: problema N+1 e strategie di fetch.
 *
 * Caso d'uso: per calcolare la classifica di un torneo iteriamo sulle partite
 * giocate e accediamo a squadraHome e squadraAway di ciascuna. Se le squadre
 * sono LAZY e non vengono caricate insieme alle partite, ogni accesso scatena
 * una query → problema N+1.
 *
 * Confrontiamo:
 *   A) LAZY ingenuo  -> 1 + (numero di squadre distinte) query: ogni squadra viene
 *                       caricata una sola volta grazie alla cache di primo livello,
 *                       quindi non 1+2N ma comunque molte query
 *   B) JOIN FETCH    -> 1 query
 *   C) EntityGraph   -> 1 query (equivalente dichiarativo)
 *
 * Eseguire con: mvn test -Dtest=N1AnalisiTest
 */
@SpringBootTest
class N1AnalisiTest {

    @Autowired private EntityManagerFactory emf;
    @Autowired private EntityManager em;
    @Autowired private TorneoRepository torneoRepo;
    @Autowired private SquadraRepository squadraRepo;
    @Autowired private PartitaRepository partitaRepo;

    private Long torneoId;
    private static final int N_SQUADRE = 10;

    @BeforeEach
    @Transactional
    void setup() {
        Torneo t = new Torneo();
        t.setNome("Test"); t.setAnno(2026);
        torneoRepo.save(t);

        List<Squadra> sq = new ArrayList<>();
        for (int i = 0; i < N_SQUADRE; i++) {
            Squadra s = new Squadra();
            s.setNome("S" + i); s.setCitta("C" + i);
            s.getTornei().add(t);
            t.getSquadre().add(s);
            squadraRepo.save(s);
            sq.add(s);
        }

        // Girone all'italiana
        Random rnd = new Random(42);
        for (int i = 0; i < sq.size(); i++) {
            for (int j = i + 1; j < sq.size(); j++) {
                Partita p = new Partita();
                p.setTorneo(t);
                p.setSquadraHome(sq.get(i));
                p.setSquadraAway(sq.get(j));
                p.setDataOra(LocalDateTime.now());
                p.setGoalsHome(rnd.nextInt(4));
                p.setGoalsAway(rnd.nextInt(4));
                p.setStato("PLAYED");
                partitaRepo.save(p);
            }
        }

        torneoId = t.getId();

        // Il setup gira nella stessa transazione del test: senza svuotare il
        // persistence context tutte le entità sarebbero già in cache di primo
        // livello e nessuna strategia farebbe query aggiuntive.
        em.flush();
        em.clear();
    }

    private Statistics statistiche() {
        Statistics s = emf.unwrap(SessionFactory.class).getStatistics();
        s.setStatisticsEnabled(true);
        s.clear();
        return s;
    }

    @Test
    @Transactional
    void strategiaA_lazyIngenuo() {
        Statistics st = statistiche();
        long t0 = System.nanoTime();

        // Carico le partite senza join fetch
        List<Partita> partite = em.createQuery(
                "SELECT p FROM Partita p WHERE p.torneo.id = :id AND p.stato = 'PLAYED'",
                Partita.class)
                .setParameter("id", torneoId)
                .getResultList();

        // Accedo alle squadre: ogni accesso = una query (proxy LAZY)
        for (Partita p : partite) {
            p.getSquadraHome().getNome();
            p.getSquadraAway().getNome();
        }

        stampa("A) LAZY INGENUO", partite.size(), st, System.nanoTime() - t0);
    }

    @Test
    @Transactional
    void strategiaB_joinFetch() {
        Statistics st = statistiche();
        long t0 = System.nanoTime();

        List<Partita> partite = partitaRepo.findGiocateByTorneo(torneoId);

        for (Partita p : partite) {
            p.getSquadraHome().getNome();
            p.getSquadraAway().getNome();
        }

        stampa("B) JOIN FETCH", partite.size(), st, System.nanoTime() - t0);
    }

    @Test
    @Transactional
    void strategiaC_entityGraph() {
        Statistics st = statistiche();
        long t0 = System.nanoTime();

        var graph = em.createEntityGraph(Partita.class);
        graph.addAttributeNodes("squadraHome", "squadraAway");

        List<Partita> partite = em.createQuery(
                "SELECT p FROM Partita p WHERE p.torneo.id = :id AND p.stato = 'PLAYED'",
                Partita.class)
                .setParameter("id", torneoId)
                .setHint("jakarta.persistence.fetchgraph", graph)
                .getResultList();

        for (Partita p : partite) {
            p.getSquadraHome().getNome();
            p.getSquadraAway().getNome();
        }

        stampa("C) ENTITY GRAPH", partite.size(), st, System.nanoTime() - t0);
    }

    private void stampa(String nome, int n, Statistics st, long ns) {
        System.out.println("\n========================================");
        System.out.println("  " + nome);
        System.out.println("  Partite:        " + n);
        System.out.println("  Query SQL:      " + st.getPrepareStatementCount());
        System.out.printf ("  Tempo:          %.2f ms%n", ns / 1_000_000.0);
        System.out.println("========================================");
    }
}
