package com.site.epets.controller;

import com.site.epets.dto.Dtos.PedidoRequest;
import com.site.epets.dto.Dtos.PedidoResponse;
import com.site.epets.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servico;

    public PedidoController(PedidoService servico) {
        this.servico = servico;
    }

    /** Finaliza o pedido. Os valores são recalculados no servidor (o front não decide o preço). */
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req) {
        PedidoResponse resposta = PedidoResponse.de(servico.criar(req));
        return ResponseEntity.created(URI.create("/api/pedidos/" + resposta.codigo())).body(resposta);
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return servico.listar().stream().map(PedidoResponse::de).toList();
    }

    @GetMapping("/{codigo}")
    public PedidoResponse buscar(@PathVariable String codigo) {
        return PedidoResponse.de(servico.buscar(codigo));
    }

    /** Avança para a próxima etapa: recebido → separação → saiu para entrega → entregue. */
    @PatchMapping("/{codigo}/status")
    public PedidoResponse avancarStatus(@PathVariable String codigo) {
        return PedidoResponse.de(servico.avancarStatus(codigo));
    }
}
