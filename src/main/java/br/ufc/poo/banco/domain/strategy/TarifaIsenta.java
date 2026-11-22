package br.ufc.poo.banco.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia de tarifa zero (utilizada em Depósitos, Saques comuns e PIX de Pessoa Física).
 */
public class TarifaIsenta implements CalculadoraTarifa {

    @Override
    public BigDecimal calcularTarifa(BigDecimal valorTransacao) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
