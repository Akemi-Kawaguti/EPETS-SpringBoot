package com.site.epets.controller;

import com.site.epets.dto.Dtos.ProdutoResponse;
import com.site.epets.dto.Dtos.VitrineResponse;
import com.site.epets.service.ProdutoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService servico;

    public ProdutoController(ProdutoService servico) {
        this.servico = servico;
    }

    /**
     * Catálogo com filtros. Cada filtro aceita vários valores, ex.:
     * /api/produtos?especie=Cães&especie=Gatos&marca=Golden&faixaPreco=ate-50&ordem=menor-preco
     */
    @GetMapping
    public List<ProdutoResponse> listar(@RequestParam(required = false) List<String> especie,
                                        @RequestParam(required = false) List<String> marca,
                                        @RequestParam(required = false) List<String> fase,
                                        @RequestParam(required = false) List<String> tipo,
                                        @RequestParam(required = false) List<String> faixaPreco,
                                        @RequestParam(required = false) String q,
                                        @RequestParam(required = false, defaultValue = "relevancia") String ordem) {
        return servico.listar(especie, marca, fase, tipo, faixaPreco, q, ordem).stream()
                .map(ProdutoResponse::de).toList();
    }

    /** Vitrines da página inicial. */
    @GetMapping("/vitrine")
    public VitrineResponse vitrine() {
        return new VitrineResponse(
                servico.favoritos().stream().map(ProdutoResponse::de).toList(),
                servico.pequenosCompanheiros().stream().map(ProdutoResponse::de).toList());
    }

    /** Sugestões da caixa de busca (máx. 5). */
    @GetMapping("/sugestoes")
    public List<ProdutoResponse> sugestoes(@RequestParam String q) {
        return servico.sugestoes(q).stream().map(ProdutoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable String id) {
        return ProdutoResponse.de(servico.buscar(id));
    }
}
