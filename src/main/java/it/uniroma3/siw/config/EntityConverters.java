package it.uniroma3.siw.config;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Senza questi convertitori, i form Thymeleaf con <select> che inviano
 * l'id come stringa non saprebbero come ricostruire l'entità.
 */
@Component
public class EntityConverters implements WebMvcConfigurer {

    private final TorneoRepository torneoRepo;
    private final SquadraRepository squadraRepo;
    private final ArbitroRepository arbitroRepo;

    public EntityConverters(TorneoRepository t, SquadraRepository s, ArbitroRepository a) {
        this.torneoRepo = t; this.squadraRepo = s; this.arbitroRepo = a;
    }

    @Override
    public void addFormatters(FormatterRegistry reg) {
        reg.addConverter(new Converter<String, Torneo>() {
            public Torneo convert(String s) {
                return s == null || s.isBlank() ? null : torneoRepo.findById(Long.valueOf(s)).orElse(null);
            }
        });
        reg.addConverter(new Converter<String, Squadra>() {
            public Squadra convert(String s) {
                return s == null || s.isBlank() ? null : squadraRepo.findById(Long.valueOf(s)).orElse(null);
            }
        });
        reg.addConverter(new Converter<String, Arbitro>() {
            public Arbitro convert(String s) {
                return s == null || s.isBlank() ? null : arbitroRepo.findById(Long.valueOf(s)).orElse(null);
            }
        });
    }
}
