package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Partita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PartitaRepository extends JpaRepository<Partita, Long> {

    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraHome JOIN FETCH p.squadraAway " +
           "WHERE p.torneo.id = :torneoId AND p.stato = 'PLAYED'")
    List<Partita> findGiocateByTorneo(Long torneoId);

    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraHome JOIN FETCH p.squadraAway " +
           "WHERE p.torneo.id = :torneoId ORDER BY p.dataOra")
    List<Partita> findByTorneo(Long torneoId);

    // partite della squadra, in casa o in trasferta
    @Query("SELECT p FROM Partita p WHERE p.squadraHome.id = :squadraId OR p.squadraAway.id = :squadraId")
    List<Partita> findBySquadra(Long squadraId);

    // dettaglio partita con tutte le associazioni in una query
    @Query("SELECT p FROM Partita p " +
           "JOIN FETCH p.squadraHome JOIN FETCH p.squadraAway JOIN FETCH p.torneo " +
           "LEFT JOIN FETCH p.arbitro " +
           "WHERE p.id = :id")
    Optional<Partita> findByIdCompleta(Long id);
}
