package it.uniroma3.siw.config;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@Profile("!test")

public class DataInit {

    @Bean
    public CommandLineRunner init(CredentialsRepository credenziali,
                                  TorneoRepository tornei,
                                  SquadraRepository squadre,
                                  ArbitroRepository arbitri,
                                  PartitaRepository partite,
                                  PasswordEncoder enc) {
        return args -> {
            if (credenziali.count() > 0) return;

            credenziali.save(nuoveCredenziali("admin", "admin123", Credentials.ADMIN_ROLE,
                    "Admin", "Root", enc));
            credenziali.save(nuoveCredenziali("mario", "mario123", Credentials.USER_ROLE,
                    "Mario", "Rossi", enc));

            Arbitro arbitro = new Arbitro();
            arbitro.setNome("Daniele"); arbitro.setCognome("Orsato"); arbitro.setCodiceArbitrale("AR-001");
            arbitri.save(arbitro);

            Torneo coppa = new Torneo();
            coppa.setNome("Coppa Lazio"); coppa.setAnno(2026);
            coppa.setDescrizione("Torneo amatoriale.");
            tornei.save(coppa);

            Squadra s1 = nuovaSquadra("Frascati FC", "Frascati", 1920);
            Squadra s2 = nuovaSquadra("Albano United", "Albano", 1945);
            Squadra s3 = nuovaSquadra("Marino Calcio", "Marino", 1932);
            for (Squadra s : new Squadra[]{s1, s2, s3}) {
                s.getTornei().add(coppa);
                coppa.getSquadre().add(s);
                squadre.save(s);
            }

            // 3 partite: 2 giocate, 1 da giocare
            partite.save(partita(coppa, s1, s2, arbitro, 2, 1, "PLAYED"));
            partite.save(partita(coppa, s2, s3, arbitro, 0, 0, "PLAYED"));
            partite.save(partita(coppa, s1, s3, arbitro, null, null, "SCHEDULED"));

            System.out.println("=== DB pronto ===");
            System.out.println("admin / admin123 (ADMIN)");
            System.out.println("mario / mario123 (USER)");
        };
    }

    private Credentials nuoveCredenziali(String username, String password, String role,
                                         String nome, String cognome, PasswordEncoder enc) {
        User u = new User();
        u.setNome(nome); u.setCognome(cognome);
        Credentials c = new Credentials();
        c.setUsername(username);
        c.setPassword(enc.encode(password));
        c.setRole(role);
        c.setUser(u); // cascade ALL salva anche lo User
        return c;
    }

    private Squadra nuovaSquadra(String nome, String citta, int anno) {
        Squadra s = new Squadra();
        s.setNome(nome); s.setCitta(citta); s.setAnnoFondazione(anno);
        return s;
    }

    private Partita partita(Torneo t, Squadra h, Squadra a, Arbitro arb,
                            Integer gh, Integer ga, String stato) {
        Partita p = new Partita();
        p.setTorneo(t); p.setSquadraHome(h); p.setSquadraAway(a); p.setArbitro(arb);
        p.setDataOra(LocalDateTime.now().plusDays(1));
        p.setLuogo("Stadio " + h.getCitta());
        p.setGoalsHome(gh); p.setGoalsAway(ga); p.setStato(stato);
        return p;
    }
}
