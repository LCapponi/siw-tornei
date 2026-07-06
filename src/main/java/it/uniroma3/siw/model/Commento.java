package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String testo;

    private LocalDateTime data;

    @ManyToOne(optional = false)
    private Partita partita;

    @ManyToOne(optional = false)
    private User user;

    @PrePersist
    void onCreate() { this.data = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public Partita getPartita() { return partita; }
    public void setPartita(Partita partita) { this.partita = partita; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
