package com.site.epets.model;

/**
 * Formas aceitas no checkout. Por segurança, a API nunca recebe nem guarda
 * número, validade ou CVV do cartão: só a forma escolhida e as parcelas.
 */
public enum FormaPagamento {
    CREDITO, DEBITO, DINHEIRO, PIX
}
