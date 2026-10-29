package com.site.epets.service;

import com.site.epets.dto.Dtos.CupomResponse;
import com.site.epets.dto.Dtos.FreteResponse;
import com.site.epets.model.ModoEntrega;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Regras de frete e cupom — as mesmas que o site aplica no navegador. */
@Service
public class CalculadoraService {

    private final BigDecimal valorFrete;
    private final String codigoCupom;
    private final BigDecimal percentualCupom;

    public CalculadoraService(@Value("${epets.frete.valor}") BigDecimal valorFrete,
                              @Value("${epets.cupom.codigo}") String codigoCupom,
                              @Value("${epets.cupom.percentual}") BigDecimal percentualCupom) {
        this.valorFrete = valorFrete;
        this.codigoCupom = codigoCupom;
        this.percentualCupom = percentualCupom;
    }

    /** Entrega local só para CEPs que começam com 01 a 05 (região atendida no site). */
    public FreteResponse consultarFrete(String cep) {
        String digitos = cep == null ? "" : cep.replaceAll("\\D", "");
        if (digitos.length() != 8) {
            throw new IllegalArgumentException("Digite um CEP com 8 números.");
        }
        String formatado = digitos.substring(0, 5) + "-" + digitos.substring(5);
        boolean local = digitos.matches("0[1-5].*");
        if (local) {
            return new FreteResponse(formatado, true, valorFrete, "até 2 dias úteis",
                    "Entrega local • até 2 dias úteis");
        }
        return new FreteResponse(formatado, false, BigDecimal.ZERO, "pronto em 2 horas",
                "Fora da área de entrega local. Você pode retirar na loja (grátis).");
    }

    public boolean atendeEntregaLocal(String cep) {
        return consultarFrete(cep).entregaLocal();
    }

    public BigDecimal frete(ModoEntrega modo) {
        return modo == ModoEntrega.RETIRADA ? BigDecimal.ZERO : valorFrete;
    }

    public CupomResponse validarCupom(String codigo, BigDecimal subtotal) {
        String normalizado = codigo == null ? "" : codigo.trim().toUpperCase();
        if (!codigoCupom.equals(normalizado)) {
            return new CupomResponse(false, normalizado, BigDecimal.ZERO,
                    "Cupom inválido. Experimente " + codigoCupom + ".");
        }
        BigDecimal desconto = subtotal.multiply(percentualCupom)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new CupomResponse(true, normalizado, desconto,
                "Cupom " + normalizado + " aplicado: " + percentualCupom.stripTrailingZeros().toPlainString()
                        + "% de desconto.");
    }

    /** Desconto do cupom, ou zero se não houver cupom / for inválido. */
    public BigDecimal desconto(String cupom, BigDecimal subtotal) {
        if (cupom == null || cupom.isBlank()) return BigDecimal.ZERO;
        return validarCupom(cupom, subtotal).desconto();
    }

    public String cupomValidoOuNull(String cupom) {
        if (cupom == null || cupom.isBlank()) return null;
        return validarCupom(cupom, BigDecimal.ZERO).valido() ? cupom.trim().toUpperCase() : null;
    }
}
