package com.site.epets.controller;

import com.site.epets.dto.Dtos.ContatoRequest;
import com.site.epets.dto.Dtos.ContatoResponse;
import com.site.epets.model.MensagemContato;
import com.site.epets.repository.MensagemContatoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Formulário "Falar com a E'PETS". */
@RestController
@RequestMapping("/api/contato")
public class ContatoController {

    private final MensagemContatoRepository repositorio;

    public ContatoController(MensagemContatoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContatoResponse enviar(@Valid @RequestBody ContatoRequest req) {
        MensagemContato salva = repositorio.save(new MensagemContato(req.nomePet(), req.especie(), req.mensagem()));
        return ContatoResponse.de(salva);
    }

    /** Lista as mensagens recebidas (para a equipe da loja). */
    @GetMapping
    public List<ContatoResponse> listar() {
        return repositorio.findAllByOrderByRecebidaEmDesc().stream().map(ContatoResponse::de).toList();
    }
}
