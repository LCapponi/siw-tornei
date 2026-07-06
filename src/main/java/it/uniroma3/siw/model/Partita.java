package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataOra;

    private String luogo;

    private Integer goalsHome;

    private Integer goalsAway;

    @Column(nullable = false)
    private String stato; // SCHEDULED, PLAYED

    // fetch LAZY esplicito: il default JPA per @ManyToOne è EAGER.
    // Chi ha bisogno delle associazioni le carica con JOIN FETCH / EntityGraph
    // (vedi PartitaRepository e l'analisi N+1).
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Torneo torneo;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Squadra squadraHome;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Squadra squadraAway;

    @ManyToOne(fetch = FetchType.LAZY)
    private Arbitro arbitro;

    @OneToMany(mappedBy = "partita", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Commento> commenti = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }
    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }
    public Integer getGoalsHome() { return goalsHome; }
    public void setGoalsHome(Integer goalsHome) { this.goalsHome = goalsHome; }
    public Integer getGoalsAway() { return goalsAway; }
    public void setGoalsAway(Integer goalsAway) { this.goalsAway = goalsAway; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    public Squadra getSquadraHome() { return squadraHome; }
    public void setSquadraHome(Squadra squadraHome) { this.squadraHome = squadraHome; }
    public Squadra getSquadraAway() { return squadraAway; }
    public void setSquadraAway(Squadra squadraAway) { this.squadraAway = squadraAway; }
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    public Set<Commento> getCommenti() { return commenti; }
    public void setCommenti(Set<Commento> commenti) { this.commenti = commenti; }
}
