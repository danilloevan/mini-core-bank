package br.ufc.poo.banco.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia de tarifa de valor fixo (utilizada em transferências TED).
 */
public class TarifaFixa implements CalculadoraTarifa {

    private final BigDecimal valorFixo;

    public TarifaFixa(BigDecimal valorFixo) {
        this.valorFixo = valorFixo.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularTarifa(BigDecimal valorTransacao) {
        return valorFixo;
    }
}
