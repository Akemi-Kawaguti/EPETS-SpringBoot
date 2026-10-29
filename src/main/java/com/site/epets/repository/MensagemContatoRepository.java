package com.site.epets.repository;

import com.site.epets.model.MensagemContato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemContatoRepository extends JpaRepository<MensagemContato, Long> {

    List<MensagemContato> findAllByOrderByRecebidaEmDesc();
}
