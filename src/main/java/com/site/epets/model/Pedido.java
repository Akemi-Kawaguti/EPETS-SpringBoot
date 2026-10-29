package com.site.epets.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    /** Código mostrado ao cliente, ex.: "EPT-2026-482". */
    @Id
    private String codigo;

    private LocalDateTime criadoEm;

    @Embedded
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_codigo")
    private List<ItemPedido> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ModoEntrega modoEntrega;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    private int parcelas;

    private String cupom;

    @Column(precision = 10, scale = 2) private BigDecimal subtotal;
    @Column(precision = 10, scale = 2) private BigDecimal desconto;
    @Column(precision = 10, scale = 2) private BigDecimal frete;
    @Column(precision = 10, scale = 2) private BigDecimal total;

    /** Valor que o cliente vai entregar em dinheiro (null se não precisa de troco). */
    @Column(precision = 10, scale = 2) private BigDecimal trocoPara;
    @Column(precision = 10, scale = 2) private BigDecimal troco;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    protected Pedido() {
    }

    public Pedido(String codigo, Cliente cliente, ModoEntrega modoEntrega,
                  FormaPagamento formaPagamento, int parcelas) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.modoEntrega = modoEntrega;
        this.formaPagamento = formaPagamento;
        this.parcelas = parcelas;
        this.criadoEm = LocalDateTime.now();
        this.status = StatusPedido.RECEBIDO;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public void definirValores(BigDecimal subtotal, BigDecimal desconto, BigDecimal frete,
                               BigDecimal total, String cupom) {
        this.subtotal = subtotal;
        this.desconto = desconto;
        this.frete = frete;
        this.total = total;
        this.cupom = cupom;
    }

    public void definirTroco(BigDecimal trocoPara, BigDecimal troco) {
        this.trocoPara = trocoPara;
        this.troco = troco;
    }

    public void avancarStatus() {
        this.status = status.proximo();
    }

    public String getCodigo() { return codigo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }
    public ModoEntrega getModoEntrega() { return modoEntrega; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public int getParcelas() { return parcelas; }
    public String getCupom() { return cupom; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDesconto() { return desconto; }
    public BigDecimal getFrete() { return frete; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getTrocoPara() { return trocoPara; }
    public BigDecimal getTroco() { return troco; }
    public StatusPedido getStatus() { return status; }
}
