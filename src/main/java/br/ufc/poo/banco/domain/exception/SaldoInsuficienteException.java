package br.ufc.poo.banco.domain.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando uma conta não possui saldo suficiente para realizar um débito.
 */
public class SaldoInsuficienteException extends NegocioException {

    public SaldoInsuficienteException(String numeroConta, BigDecimal saldoAtual, BigDecimal valorSolicitado) {
        super(String.format(
                "Saldo insuficiente na conta %s. Saldo atual: R$ %s | Valor necessário: R$ %s",
                numeroConta, saldoAtual, valorSolicitado
        ));
    }
}
