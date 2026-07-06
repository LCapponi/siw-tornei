package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Giocatore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {
	long countBySquadraId(Long squadraId);
}
