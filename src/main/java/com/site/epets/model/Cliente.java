package com.site.epets.model;

import jakarta.persistence.Embeddable;

/** Contato e endereço informados no checkout (gravados junto com o pedido). */
@Embeddable
public class Cliente {

    private String nome;
    private String email;
    private String celular;
    private String cep;
    private String rua;
    private String complemento;
    private String bairro;
    private String cidade;

    protected Cliente() {
    }

    public Cliente(String nome, String email, String celular, String cep,
                   String rua, String complemento, String bairro, String cidade) {
        this.nome = nome;
        this.email = email;
        this.celular = celular;
        this.cep = cep;
        this.rua = rua;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCelular() { return celular; }
    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
}
