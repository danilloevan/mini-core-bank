package br.ufc.poo.banco.domain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia de tarifa percentual com teto máximo (utilizada em PIX comercial de Pessoa Jurídica).
 */
public class TarifaPercentual implements CalculadoraTarifa {

    private final BigDecimal percentual;
    private final BigDecimal tetoMaximo;

    public TarifaPercentual(BigDecimal percentual, BigDecimal tetoMaximo) {
        this.percentual = percentual;
        this.tetoMaximo = tetoMaximo;
    }

    @Override
    public BigDecimal calcularTarifa(BigDecimal valorTransacao) {
        if (valorTransacao == null || valorTransacao.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal calculada = valorTransacao.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
        if (tetoMaximo != null && calculada.compareTo(tetoMaximo) > 0) {
            return tetoMaximo.setScale(2, RoundingMode.HALF_UP);
        }
        return calculada;
    }
}
