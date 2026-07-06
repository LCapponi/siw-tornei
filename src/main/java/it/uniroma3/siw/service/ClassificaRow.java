package it.uniroma3.siw.service;

/**
 * View-model (riga di classifica) per il caso d'uso "classifica del torneo".
 * Sta nel package service perché è un modello di lettura derivato, non
 * un'entità persistente: non va su DB, viene calcolato a partire dalle partite.
 *
 * NB: si trova nello stesso package di TorneoService, quindi quest'ultimo
 * può usarlo senza import aggiuntivi (com'è già scritto nel tuo codice).
 */
public class ClassificaRow {

    private final String nome;
    private int partiteGiocate;
    private int vittorie;
    private int pareggi;
    private int sconfitte;
    private int golFatti;
    private int golSubiti;
    private int punti;

    public ClassificaRow(String nome) {
        this.nome = nome;
    }

    /**
     * Registra il risultato di una partita dal punto di vista di QUESTA squadra.
     * @param gf gol fatti dalla squadra in quella partita
     * @param gs gol subiti dalla squadra in quella partita
     */
    public void aggiungiRisultato(int gf, int gs) {
        this.partiteGiocate++;
        this.golFatti += gf;
        this.golSubiti += gs;
        if (gf > gs) {
            this.vittorie++;
            this.punti += 3;
        } else if (gf == gs) {
            this.pareggi++;
            this.punti += 1;
        } else {
            this.sconfitte++;
        }
    }

    public String getNome()            { return nome; }
    public int getPartiteGiocate()     { return partiteGiocate; }
    public int getVittorie()           { return vittorie; }
    public int getPareggi()            { return pareggi; }
    public int getSconfitte()          { return sconfitte; }
    public int getGolFatti()           { return golFatti; }
    public int getGolSubiti()          { return golSubiti; }
    public int getDifferenzaReti()     { return golFatti - golSubiti; }
    public int getPunti()              { return punti; }
}