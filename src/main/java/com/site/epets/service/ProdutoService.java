package com.site.epets.service;

import com.site.epets.exception.NaoEncontradoException;
import com.site.epets.model.Produto;
import com.site.epets.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/** Catálogo com os mesmos filtros e ordenações da tela "Tudo para o seu companheiro". */
@Service
public class ProdutoService {

    private final ProdutoRepository repositorio;

    public ProdutoService(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Produto> listar(List<String> especies, List<String> marcas, List<String> fases,
                                List<String> tipos, List<String> faixasPreco, String busca, String ordem) {
        Predicate<Produto> filtro = p -> true;

        if (temValor(especies)) {
            filtro = filtro.and(p -> especies.stream().anyMatch(p::atendeEspecie));
        }
        if (temValor(marcas)) {
            filtro = filtro.and(p -> marcas.stream().anyMatch(m -> m.equalsIgnoreCase(p.getMarca())));
        }
        if (temValor(fases)) {
            filtro = filtro.and(p -> fases.stream().anyMatch(f -> f.equalsIgnoreCase(p.getFase())));
        }
        if (temValor(tipos)) {
            filtro = filtro.and(p -> tipos.stream().anyMatch(t ->
                    "Acessórios".equalsIgnoreCase(t) ? p.isAcessorio() : t.equalsIgnoreCase(p.getTipo())));
        }
        if (temValor(faixasPreco)) {
            filtro = filtro.and(p -> faixasPreco.stream().anyMatch(f -> naFaixa(p.getPreco(), f)));
        }
        if (busca != null && !busca.isBlank()) {
            String termo = normalizar(busca);
            filtro = filtro.and(p -> normalizar(String.join(" ", p.getNome(), p.getMarca(),
                    p.getEspecie(), p.getDetalhe(), p.getTipo())).contains(termo));
        }

        return repositorio.findAll().stream()
                .filter(filtro)
                .sorted(ordenacao(ordem))
                .toList();
    }

    /** Até 5 sugestões para a caixa de busca do topo. */
    public List<Produto> sugestoes(String termo) {
        if (termo == null || termo.isBlank()) return List.of();
        return listar(null, null, null, null, null, termo, null).stream().limit(5).toList();
    }

    public Produto buscar(String id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Produto não encontrado: " + id));
    }

    public List<Produto> favoritos() {
        return repositorio.findByOrdemFavoritoNotNullOrderByOrdemFavorito();
    }

    public List<Produto> pequenosCompanheiros() {
        return repositorio.findByOrdemPequenosNotNullOrderByOrdemPequenos();
    }

    // Faixas: "ate-50", "50-100", "acima-100" (também aceita os rótulos do site).
    private boolean naFaixa(BigDecimal preco, String faixa) {
        String f = normalizar(faixa);
        double v = preco.doubleValue();
        if (f.startsWith("ate")) return v <= 50;
        if (f.startsWith("acima")) return v > 100;
        if (f.contains("50") && f.contains("100")) return v > 50 && v <= 100;
        return true;
    }

    private Comparator<Produto> ordenacao(String ordem) {
        if (ordem == null) ordem = "relevancia";
        return switch (ordem) {
            case "menor-preco" -> Comparator.comparing(Produto::getPreco);
            case "maior-preco" -> Comparator.comparing(Produto::getPreco).reversed();
            case "nome" -> Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER);
            // Relevância = ordem de cadastro, igual ao site
            default -> (a, b) -> 0;
        };
    }

    private static boolean temValor(List<String> lista) {
        return lista != null && !lista.isEmpty();
    }

    static String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase().trim();
    }
}
