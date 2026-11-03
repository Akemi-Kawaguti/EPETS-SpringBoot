package com.site.epets.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Mensagem do formulário "Vamos conversar?" / "Falar com a E'PETS". */
@Entity
@Table(name = "MensagemContato")
@Getter
@Setter
@NoArgsConstructor
public class MensagemContato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Valid
    private String nomePet;

    @Column
    @Valid
    private String especie;

    @Column(length = 1000)
    @Valid
    private String mensagem;

    @Column
    @Valid
    private LocalDateTime recebidaEm;

}
