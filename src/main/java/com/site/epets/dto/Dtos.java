package com.site.epets.dto;

import com.site.epets.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/** Objetos de entrada e saída da API (records do Java 21). */
public final class Dtos {

    private Dtos() {
    }

    // ---------- Produtos ----------

    public record ProdutoResponse(String id, String nome, String marca, String especie, String fase,
                                  String tipo, String detalhe, BigDecimal preco, String imagem,
                                  boolean acessorio, String categoria, int estoque) {

        public static ProdutoResponse de(Produto p) {
            return new ProdutoResponse(p.getId(), p.getNome(), p.getMarca(), p.getEspecie(), p.getFase(),
                    p.getTipo(), p.getDetalhe(), p.getPreco(), p.getImagem(), p.isAcessorio(),
                    categoria(p), p.getEstoque());
        }

        /** Mesmo rótulo que o site mostra no card (ex.: "Cães · Adultos"). */
        private static String categoria(Produto p) {
            if ("Rações e alimentos".equals(p.getTipo())) {
                return p.getEspecie() + " · " + (p.getFase() != null ? p.getFase() : "Todas as fases");
            }
            return p.getEspecie() + " · " + ("Brinquedos".equals(p.getTipo()) ? "Brinquedos" : "Alimentação");
        }
    }

    public record VitrineResponse(List<ProdutoResponse> favoritos, List<ProdutoResponse> pequenosCompanheiros) {
    }

    // ---------- Carrinho / cálculos ----------

    public record ItemRequest(@NotBlank String produtoId,
                              @Min(1) @Max(20) int quantidade) {
    }

    public record ResumoRequest(@NotEmpty @Valid List<ItemRequest> itens,
                                String cupom,
                                ModoEntrega modoEntrega) {
    }

    public record ResumoResponse(int unidades, BigDecimal subtotal, BigDecimal desconto,
                                 BigDecimal frete, BigDecimal total, String cupomAplicado) {
    }

    public record FreteResponse(String cep, boolean entregaLocal, BigDecimal valor,
                                String prazo, String mensagem) {
    }

    public record CupomRequest(@NotBlank String codigo, @NotNull @Min(0) BigDecimal subtotal) {
    }

    public record CupomResponse(boolean valido, String codigo, BigDecimal desconto, String mensagem) {
    }

    // ---------- Pedidos ----------

    public record ClienteRequest(@NotBlank @Size(min = 3) String nome,
                                 @NotBlank @Email String email,
                                 @NotBlank @Pattern(regexp = "\\(?\\d{2}\\)? ?\\d{4,5}-?\\d{4}",
                                         message = "celular inválido, use (11) 90000-0000") String celular,
                                 @NotBlank @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 números") String cep,
                                 String rua, String complemento, String bairro, String cidade) {

        public Cliente paraEntidade() {
            return new Cliente(nome, email, celular, cep, rua, complemento, bairro, cidade);
        }
    }

    public record PedidoRequest(@NotNull @Valid ClienteRequest cliente,
                                @NotEmpty @Valid List<ItemRequest> itens,
                                @NotNull ModoEntrega modoEntrega,
                                @NotNull FormaPagamento formaPagamento,
                                @Min(1) @Max(6) Integer parcelas,
                                String cupom,
                                BigDecimal trocoPara) {
    }

    public record ItemPedidoResponse(String produtoId, String nome, String detalhe, String imagem,
                                     int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {

        public static ItemPedidoResponse de(ItemPedido i) {
            Produto p = i.getProduto();
            return new ItemPedidoResponse(p.getId(), p.getNome(), p.getDetalhe(), p.getImagem(),
                    i.getQuantidade(), i.getPrecoUnitario(), i.getSubtotal());
        }
    }

    public record EtapaResponse(String etapa, boolean concluida, boolean atual) {
    }

    public record PedidoResponse(String codigo, LocalDateTime criadoEm, Cliente cliente,
                                 List<ItemPedidoResponse> itens, ModoEntrega modoEntrega,
                                 FormaPagamento formaPagamento, int parcelas, BigDecimal valorParcela,
                                 String cupom, BigDecimal subtotal, BigDecimal desconto, BigDecimal frete,
                                 BigDecimal total, BigDecimal trocoPara, BigDecimal troco,
                                 StatusPedido status, String statusDescricao, List<EtapaResponse> etapas) {

        public static PedidoResponse de(Pedido p) {
            List<EtapaResponse> etapas = Arrays.stream(StatusPedido.values())
                    .map(s -> new EtapaResponse(etapa(s, p.getModoEntrega()),
                            s.ordinal() <= p.getStatus().ordinal(),
                            s == p.getStatus()))
                    .toList();
            BigDecimal valorParcela = p.getTotal()
                    .divide(BigDecimal.valueOf(p.getParcelas()), 2, java.math.RoundingMode.HALF_UP);
            return new PedidoResponse(p.getCodigo(), p.getCriadoEm(), p.getCliente(),
                    p.getItens().stream().map(ItemPedidoResponse::de).toList(),
                    p.getModoEntrega(), p.getFormaPagamento(), p.getParcelas(), valorParcela,
                    p.getCupom(), p.getSubtotal(), p.getDesconto(), p.getFrete(), p.getTotal(),
                    p.getTrocoPara(), p.getTroco(), p.getStatus(),
                    etapa(p.getStatus(), p.getModoEntrega()), etapas);
        }

        /** Na retirada, "Saiu para entrega" vira "Pronto para retirada", como no chat do site. */
        private static String etapa(StatusPedido s, ModoEntrega modo) {
            if (s == StatusPedido.SAIU_PARA_ENTREGA && modo == ModoEntrega.RETIRADA) return "Pronto para retirada";
            if (s == StatusPedido.ENTREGUE && modo == ModoEntrega.RETIRADA) return "Retirado";
            return s.getDescricao();
        }
    }

    // ---------- Contato ----------

    public record ContatoRequest(@NotBlank String nomePet,
                                 @NotBlank String especie,
                                 @Size(max = 1000) String mensagem) {
    }

    public record ContatoResponse(Long id, String nomePet, String especie, String mensagem,
                                  LocalDateTime recebidaEm, String resposta) {

        public static ContatoResponse de(MensagemContato m) {
            return new ContatoResponse(m.getId(), m.getNomePet(), m.getEspecie(), m.getMensagem(),
                    m.getRecebidaEm(), "Recebemos a sua mensagem sobre " + m.getNomePet() + "!");
        }
    }

    // ---------- Erros ----------

    public record ErroResponse(int status, String erro, List<String> detalhes) {
    }
}
