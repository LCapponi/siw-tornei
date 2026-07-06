package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import it.uniroma3.siw.validation.NotFutureYear;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Squadra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotFutureYear
    private Integer annoFondazione;

    private String citta;
    @JsonIgnore
    @ManyToMany(mappedBy = "squadre")
    private Set<Torneo> tornei = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "squadra", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Giocatore> giocatori = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnnoFondazione() { return annoFondazione; }
    public void setAnnoFondazione(Integer annoFondazione) { this.annoFondazione = annoFondazione; }
    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }
    public Set<Torneo> getTornei() { return tornei; }
    public void setTornei(Set<Torneo> tornei) { this.tornei = tornei; }
    public Set<Giocatore> getGiocatori() { return giocatori; }
    public void setGiocatori(Set<Giocatore> giocatori) { this.giocatori = giocatori; }
}
