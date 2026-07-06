# Analisi sperimentale: problema N+1

## Obiettivo

Dimostrare l'impatto delle strategie di accesso ai dati sul calcolo della
classifica di un torneo, evidenziando il problema delle N+1 query.

## Setup

- Torneo con **10 squadre**, girone all'italiana di sola andata → 45 partite.
- DB H2 in-memory (per riproducibilità).
- Statistiche di Hibernate attive per contare le query.

Il calcolo della classifica richiede, per ogni partita, di accedere alle due
squadre coinvolte.

## Le 3 strategie

**A) LAZY ingenuo** — query JPQL semplice sulle partite. Le squadre sono
associazioni `@ManyToOne` con `fetch = LAZY` esplicito (il default JPA per
`@ManyToOne` sarebbe EAGER), quindi proxy. Quando il codice fa
`p.getSquadraHome().getNome()`, Hibernate emette una query per recuperare
la squadra. Nota: grazie alla cache di primo livello ogni squadra viene
caricata una sola volta, quindi il costo non è 1 + 2N ma
1 + (numero di squadre distinte) — con 10 squadre: 11 query.

**B) JOIN FETCH** — query JPQL con `JOIN FETCH` sulle due squadre.
Le squadre sono caricate insieme alle partite in una sola SELECT. Risultato:
1 query.

**C) EntityGraph** — equivalente dichiarativo: si dichiara quali attributi
caricare eagerly via `EntityGraph`, senza riscrivere la query. Risultato:
1 query.

## Risultati

Eseguiti con `mvnw test -Dtest=N1AnalisiTest` (45 partite, 10 squadre):

| Strategia        | Query SQL | Tempo (ms) |
|------------------|-----------|------------|
| A) LAZY INGENUO  | 11        | 10,82      |
| B) JOIN FETCH    | 1         | 43,70 *    |
| C) ENTITY GRAPH  | 1         | 11,38      |

\* Il tempo di B è gonfiato perché JUnit lo ha eseguito per primo: paga il
warm-up di JIT e H2 (la strategia C, identica come SQL, impiega ~11 ms).
Su H2 in-memory i tempi sono comunque poco significativi: la metrica robusta
è il **numero di query**, perché su un DB reale/remoto ogni query paga il
round-trip di rete (11 round-trip contro 1).

## Conclusione

La strategia LAZY funziona ma esegue molte più query, soprattutto su DB
remoto dove ogni query ha latenza di rete. JOIN FETCH e EntityGraph
risolvono il problema con una sola query.

Nel codice di produzione (`PartitaRepository.findGiocateByTorneo`) ho
adottato JOIN FETCH come strategia di default per il calcolo classifica.
