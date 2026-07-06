package it.uniroma3.siw.exception;

public class DuplicateTorneoException extends RuntimeException {

    public DuplicateTorneoException(String nome, Integer anno) {
        super("Il torneo '" + nome + "' (" + anno + ") è già presente nel sistema");
    }
}
