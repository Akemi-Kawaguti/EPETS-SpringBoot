package com.site.epets.controller;

import com.site.epets.dto.Dtos.*;
import com.site.epets.service.CalculadoraService;
import com.site.epets.service.PedidoService;
import com.site.epets.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/** Cálculos da sacola: resumo, frete por CEP e cupom. */
@RestController
@RequestMapping("/api")
public class CarrinhoController {

    private final PedidoService pedidos;
    private final CalculadoraService calculadora;

    public CarrinhoController(com.site.epets.service.PedidoService pedidos, CalculadoraService calculadora) {
        this.pedidos = pedidos;
        this.calculadora = calculadora;
    }

    @PostMapping("/carrinho/resumo")
    public ResumoResponse resumo(@Valid @RequestBody ResumoRequest req) {
        return pedidos.resumir(req);
    }

    @GetMapping("/frete/{cep}")
    public FreteResponse frete(@PathVariable String cep) {
        return calculadora.consultarFrete(cep);
    }

    @PostMapping("/cupons/validar")
    public CupomResponse cupom(@Valid @RequestBody CupomRequest req) {
        return calculadora.validarCupom(req.codigo(), req.subtotal());
    }
}
