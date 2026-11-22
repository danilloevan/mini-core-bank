package br.ufc.poo.banco.domain.strategy;

import java.math.BigDecimal;

/**
 * Interface CalculadoraTarifa (Requisito 2.d: Interfaces e Polimorfismo).
 * Define o contrato para o cálculo polimórfico de tarifas de transações bancárias (Strategy Pattern).
 */
public interface CalculadoraTarifa {

    BigDecimal calcularTarifa(BigDecimal valorTransacao);
}
