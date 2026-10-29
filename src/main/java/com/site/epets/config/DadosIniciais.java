package com.site.epets.config;

import com.site.epets.model.Produto;
import com.site.epets.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Carrega no banco os mesmos 9 produtos do site ao iniciar a API. */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final ProdutoRepository produtos;

    public DadosIniciais(ProdutoRepository produtos) {
        this.produtos = produtos;
    }

    @Override
    public void run(String... args) {
        if (produtos.count() > 0) return;

        String racao = "Rações e alimentos";
        produtos.saveAll(List.of(
            new Produto("golden-adultos", "Golden Fórmula Cães Adultos", "Golden", "Cães", "Adultos", racao,
                    "Frango e arroz • 15 kg", "149.90", "img/p-golden-adultos.jpg", false, 1, null, 30),
            new Produto("premier-castrados", "PremieR Gatos Castrados", "PremieR", "Gatos", "Castrados", racao,
                    "Salmão • 7,5 kg", "89.90", "img/p-premier-castrados.jpg", false, 2, null, 25),
            new Produto("magnus", "Magnus Todo Dia Cães Adultos", "Magnus", "Cães", "Adultos", racao,
                    "Carne • 15 kg", "119.90", "img/p-magnus.jpg", false, 3, null, 25),
            new Produto("golden-filhotes", "Golden Fórmula Cães Filhotes", "Golden", "Cães", "Filhotes", racao,
                    "Frango e arroz • 15 kg", "64.90", "img/p-golden-filhotes.jpg", false, 4, null, 20),
            new Produto("golden-gatos", "Golden Gatos Filhotes", "Golden", "Gatos", "Filhotes", racao,
                    "Frango • 1 kg", "32.90", "img/p-golden-gatos.jpg", false, null, null, 40),
            new Produto("nutropica", "Nutrópica Hamster", "Nutrópica", "Roedores", "Adultos", racao,
                    "Alimento extrusado • 300 g", "29.90", "img/p-nutropica.jpg", false, null, 1, 35),
            new Produto("alcon", "Alcon Club Curió", "Alcon", "Aves silvestres", "Adultos", racao,
                    "Alimento para curiós • 325 g", "24.90", "img/p-alcon.jpg", false, null, 2, 35),
            new Produto("bola", "Bola de borracha para cães", "E’PETS", "Cães", null, "Brinquedos",
                    "Borracha • 7,5 cm • Azul", "29.90", "img/p-bola.jpg", true, null, 3, 50),
            new Produto("comedouro", "Comedouro de inox", "E’PETS", "Cães e gatos", null, "Comedouros",
                    "Antiderrapante • 946 ml", "34.90", "img/p-comedouro.jpg", true, null, 4, 50)
        ));
    }
}
