package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Squadra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SquadraRepository extends JpaRepository<Squadra, Long> {

    @Query("SELECT s FROM Squadra s LEFT JOIN FETCH s.giocatori WHERE s.id = :id")
    Optional<Squadra> findByIdConGiocatori(Long id);
}
