package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Arbitro;
import it.uniroma3.siw.repository.ArbitroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArbitroService {

    private final ArbitroRepository arbitroRepo;

    public ArbitroService(ArbitroRepository arbitroRepo) {
        this.arbitroRepo = arbitroRepo;
    }

    @Transactional(readOnly = true)
    public List<Arbitro> listaArbitri() { return arbitroRepo.findAll(); }

    @Transactional
    public Arbitro salvaArbitro(Arbitro a) { return arbitroRepo.save(a); }
}
