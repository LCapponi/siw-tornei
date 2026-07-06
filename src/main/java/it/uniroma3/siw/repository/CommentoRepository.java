package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Commento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentoRepository extends JpaRepository<Commento, Long> {

    @Query("SELECT c FROM Commento c JOIN FETCH c.user WHERE c.partita.id = :partitaId ORDER BY c.data")
    List<Commento> findByPartita(Long partitaId);
}
