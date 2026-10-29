package com.site.epets.model;

/** Etapas do pedido, na mesma ordem da linha do tempo do site. */
public enum StatusPedido {
    RECEBIDO("Pedido recebido"),
    EM_SEPARACAO("Em separação"),
    SAIU_PARA_ENTREGA("Saiu para entrega"),
    ENTREGUE("Entregue");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusPedido proximo() {
        return this == ENTREGUE ? ENTREGUE : values()[ordinal() + 1];
    }
}
