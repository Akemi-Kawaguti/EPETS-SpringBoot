package com.site.epets.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Mensagem do formulário "Vamos conversar?" / "Falar com a E'PETS". */
@Entity
public class MensagemContato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomePet;
    private String especie;

    @Column(length = 1000)
    private String mensagem;

    private LocalDateTime recebidaEm;

    protected MensagemContato() {
    }

    public MensagemContato(String nomePet, String especie, String mensagem) {
        this.nomePet = nomePet;
        this.especie = especie;
        this.mensagem = mensagem;
        this.recebidaEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getNomePet() { return nomePet; }
    public String getEspecie() { return especie; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getRecebidaEm() { return recebidaEm; }
}
