# SIW Tornei

Progetto docente per SIW: gestione tornei di calcio amatoriale.

## Stack
Spring Boot 3.5 · Java 21 · JPA/Hibernate · PostgreSQL · Thymeleaf · React (in `siw-tornei-front`)

## Avvio

```sql
-- una tantum, da psql o pgAdmin:
CREATE DATABASE tornei;
```

```
# backend (porta 8080)
.\mvnw.cmd spring-boot:run

# frontend React (porta 5173) — prima volta: npm install
cd siw-tornei-front
npm install
npm run dev
```

## Login demo (creati da DataInit al primo avvio su DB vuoto)
- `admin` / `admin123` (ADMIN)
- `mario` / `mario123` (USER)

## Entità (8)
`Torneo`, `Squadra`, `Giocatore`, `Partita`, `Arbitro`, `Commento`, `User`, `Credentials`

## Casi d'uso

**Pubblici**: elenco tornei, dettaglio torneo con calendario partite, elenco
squadre, dettaglio squadra con giocatori, dettaglio partita, classifica del
torneo (anche in React, con ricerca e ordinamento).

**Utenti registrati**: visualizzazione commenti, inserimento e modifica di un
proprio commento a una partita (solo i propri: controllo nel service).

**Admin**: creazione/modifica/eliminazione di tornei, squadre, giocatori e
partite; registrazione dei risultati.

## Analisi N+1 (obbligatoria, sezione 8.2)

Vedi `ANALISI-N1.md` (con i risultati misurati) e il test `N1AnalisiTest.java`.
Esecuzione: `.\mvnw.cmd test -Dtest=N1AnalisiTest`

## Note architetturali
- Architettura a livelli: controller → service (`@Transactional`, logica di
  business) → repository JPA → PostgreSQL.
- `ddl-auto=update` + `DataInit` idempotente: i dati sopravvivono ai riavvii.
- `@ManyToOne` con `fetch = LAZY` esplicito; le pagine caricano le associazioni
  con JOIN FETCH nei repository (anti N+1).
- Update via dirty checking: mai merge di oggetti parziali ricostruiti dai form.
- React consuma `/rest/tornei` e `/rest/tornei/{id}/classifica` (JSON, CORS
  abilitato per http://localhost:5173).
