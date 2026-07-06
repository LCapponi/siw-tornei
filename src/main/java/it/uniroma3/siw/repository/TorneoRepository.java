package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Torneo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {

    // controllo duplicati (nome + anno)
    boolean existsByNomeAndAnno(String nome, Integer anno);
    boolean existsByNomeAndAnnoAndIdNot(String nome, Integer anno, Long id);

    // ===== Le 3 strategie per l'analisi N+1 (richiesta dal PDF) =====

    // Strategia 1: LAZY (findById ereditato) -> N+1 query accedendo a squadre/partite

    // Strategia 2: JOIN FETCH
    @Query("SELECT t FROM Torneo t LEFT JOIN FETCH t.squadre WHERE t.id = :id")
    Optional<Torneo> findByIdJoinFetch(Long id);

    // Strategia 3: EntityGraph
    @EntityGraph(attributePaths = {"squadre"})
    @Query("SELECT t FROM Torneo t WHERE t.id = :id")
    Optional<Torneo> findByIdEntityGraph(Long id);
}
