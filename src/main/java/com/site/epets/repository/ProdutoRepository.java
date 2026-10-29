package com.site.epets.repository;

import com.site.epets.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, String> {

    List<Produto> findByOrdemFavoritoNotNullOrderByOrdemFavorito();

    List<Produto> findByOrdemPequenosNotNullOrderByOrdemPequenos();
}
//sudo git pull