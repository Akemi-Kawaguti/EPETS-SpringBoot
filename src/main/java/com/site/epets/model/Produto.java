package com.site.epets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Produto {

    /** Identificador legível, igual ao usado no front (ex.: "golden-adultos"). */
    @Id
    private String id;

    @Column(nullable = false)
    private String nome;

    private String marca;

    /** Cães, Gatos, Roedores, Aves silvestres ou "Cães e gatos". */
    private String especie;

    /** Filhotes, Adultos, Castrados ou null para acessórios. */
    private String fase;

    /** Rações e alimentos, Brinquedos ou Comedouros. */
    private String tipo;

    /** Descrição curta (sabor, peso, tamanho). */
    private String detalhe;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    /** Caminho da imagem no front (ex.: "img/p-magnus.jpg"). */
    private String imagem;

    private boolean acessorio;

    /** Ordem na vitrine "Os favoritos do potinho" (null = não aparece). */
    private Integer ordemFavorito;

    /** Ordem na vitrine "Pequenos companheiros" (null = não aparece). */
    private Integer ordemPequenos;

    private int estoque;

    protected Produto() {
    }

    public Produto(String id, String nome, String marca, String especie, String fase, String tipo,
                   String detalhe, String preco, String imagem, boolean acessorio,
                   Integer ordemFavorito, Integer ordemPequenos, int estoque) {
        this.id = id;
        this.nome = nome;
        this.marca = marca;
        this.especie = especie;
        this.fase = fase;
        this.tipo = tipo;
        this.detalhe = detalhe;
        this.preco = new BigDecimal(preco);
        this.imagem = imagem;
        this.acessorio = acessorio;
        this.ordemFavorito = ordemFavorito;
        this.ordemPequenos = ordemPequenos;
        this.estoque = estoque;
    }

    /** Verdadeiro se o produto serve para a espécie pedida (comedouro serve para cães e gatos). */
    public boolean atendeEspecie(String especiePedida) {
        if (especiePedida == null || especiePedida.isBlank()) return true;
        if (especiePedida.equalsIgnoreCase(especie)) return true;
        return "Cães e gatos".equals(especie)
                && ("Cães".equalsIgnoreCase(especiePedida) || "Gatos".equalsIgnoreCase(especiePedida));
    }

    public void baixarEstoque(int quantidade) {
        if (quantidade > estoque) {
            throw new IllegalStateException("Estoque insuficiente para " + nome + " (disponível: " + estoque + ")");
        }
        estoque -= quantidade;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Produto outro && id != null && id.equals(outro.id));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getMarca() { return marca; }
    public String getEspecie() { return especie; }
    public String getFase() { return fase; }
    public String getTipo() { return tipo; }
    public String getDetalhe() { return detalhe; }
    public BigDecimal getPreco() { return preco; }
    public String getImagem() { return imagem; }
    public boolean isAcessorio() { return acessorio; }
    public Integer getOrdemFavorito() { return ordemFavorito; }
    public Integer getOrdemPequenos() { return ordemPequenos; }
    public int getEstoque() { return estoque; }

    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public void setEstoque(int estoque) { this.estoque = estoque; }
}
