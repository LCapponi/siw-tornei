package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import it.uniroma3.siw.validation.NotFutureYear;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotNull
    @Min(1900)
    @NotFutureYear
    @Column(nullable = false)
    private Integer anno;

    @Column(length = 1000)
    private String descrizione;

    @ManyToMany
    @JoinTable(name = "torneo_squadra",
            joinColumns = @JoinColumn(name = "torneo_id"),
            inverseJoinColumns = @JoinColumn(name = "squadra_id"))
    private Set<Squadra> squadre = new HashSet<>();

    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Partita> partite = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Set<Squadra> getSquadre() { return squadre; }
    public void setSquadre(Set<Squadra> squadre) { this.squadre = squadre; }
    public Set<Partita> getPartite() { return partite; }
    public void setPartite(Set<Partita> partite) { this.partite = partite; }
}
