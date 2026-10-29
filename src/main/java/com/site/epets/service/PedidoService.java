package com.site.epets.service;

import com.site.epets.dto.Dtos.ItemRequest;
import com.site.epets.dto.Dtos.PedidoRequest;
import com.site.epets.dto.Dtos.ResumoRequest;
import com.site.epets.dto.Dtos.ResumoResponse;
import com.site.epets.exception.NaoEncontradoException;
import com.site.epets.exception.RegraNegocioException;
import com.site.epets.model.*;
import com.site.epets.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final ProdutoService produtos;
    private final CalculadoraService calculadora;

    public PedidoService(PedidoRepository pedidos, ProdutoService produtos, CalculadoraService calculadora) {
        this.pedidos = pedidos;
        this.produtos = produtos;
        this.calculadora = calculadora;
    }

    /** Resumo da sacola: o servidor recalcula tudo a partir dos preços do banco. */
    public ResumoResponse resumir(ResumoRequest req) {
        Map<Produto, Integer> itens = agrupar(req.itens());
        BigDecimal subtotal = subtotal(itens);
        BigDecimal desconto = calculadora.desconto(req.cupom(), subtotal);
        BigDecimal frete = calculadora.frete(req.modoEntrega() == null ? ModoEntrega.ENTREGA : req.modoEntrega());
        BigDecimal total = subtotal.subtract(desconto).add(frete).max(BigDecimal.ZERO);
        int unidades = itens.values().stream().mapToInt(Integer::intValue).sum();
        return new ResumoResponse(unidades, subtotal, desconto, frete, total, calculadora.cupomValidoOuNull(req.cupom()));
    }

    @Transactional
    public Pedido criar(PedidoRequest req) {
        if (req.modoEntrega() == ModoEntrega.ENTREGA && !calculadora.atendeEntregaLocal(req.cliente().cep())) {
            throw new RegraNegocioException("CEP fora da área de entrega local. Escolha retirar na loja.");
        }

        int parcelas = req.formaPagamento() == FormaPagamento.CREDITO && req.parcelas() != null ? req.parcelas() : 1;
        Pedido pedido = new Pedido(novoCodigo(), req.cliente().paraEntidade(),
                req.modoEntrega(), req.formaPagamento(), parcelas);

        Map<Produto, Integer> itens = agrupar(req.itens());
        itens.forEach((produto, qtd) -> {
            produto.baixarEstoque(qtd);
            pedido.adicionarItem(new ItemPedido(produto, qtd));
        });

        BigDecimal subtotal = subtotal(itens);
        BigDecimal desconto = calculadora.desconto(req.cupom(), subtotal);
        BigDecimal frete = calculadora.frete(req.modoEntrega());
        BigDecimal total = subtotal.subtract(desconto).add(frete).max(BigDecimal.ZERO);
        pedido.definirValores(subtotal, desconto, frete, total, calculadora.cupomValidoOuNull(req.cupom()));

        if (req.formaPagamento() == FormaPagamento.DINHEIRO && req.trocoPara() != null) {
            if (req.trocoPara().compareTo(total) < 0) {
                throw new RegraNegocioException("O valor para troco precisa ser maior que o total (R$ " + total + ").");
            }
            pedido.definirTroco(req.trocoPara(), req.trocoPara().subtract(total));
        }

        return pedidos.save(pedido);
    }

    public Pedido buscar(String codigo) {
        return pedidos.findById(codigo.toUpperCase())
                .orElseThrow(() -> new NaoEncontradoException("Pedido não encontrado: " + codigo));
    }

    public List<Pedido> listar() {
        return pedidos.findAllByOrderByCriadoEmDesc();
    }

    /** Avança uma etapa (botão "Atualizar status" do chat de acompanhamento). */
    @Transactional
    public Pedido avancarStatus(String codigo) {
        Pedido pedido = buscar(codigo);
        pedido.avancarStatus();
        return pedidos.save(pedido);
    }

    // ---------- auxiliares ----------

    /** Junta itens repetidos e valida produto e quantidade (máx. 20 por produto, como no site). */
    private Map<Produto, Integer> agrupar(List<ItemRequest> itensReq) {
        Map<Produto, Integer> itens = new LinkedHashMap<>();
        for (ItemRequest i : itensReq) {
            Produto p = produtos.buscar(i.produtoId());
            itens.merge(p, i.quantidade(), Integer::sum);
        }
        itens.forEach((p, q) -> {
            if (q > 20) throw new RegraNegocioException("Máximo de 20 unidades por produto: " + p.getNome());
        });
        return itens;
    }

    private BigDecimal subtotal(Map<Produto, Integer> itens) {
        return itens.entrySet().stream()
                .map(e -> e.getKey().getPreco().multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Gera "EPT-2026-123" sem repetir. */
    private String novoCodigo() {
        String codigo;
        do {
            int n = ThreadLocalRandom.current().nextInt(100, 1000);
            codigo = "EPT-" + Year.now().getValue() + "-" + n;
        } while (pedidos.existsById(codigo));
        return codigo;
    }
}
