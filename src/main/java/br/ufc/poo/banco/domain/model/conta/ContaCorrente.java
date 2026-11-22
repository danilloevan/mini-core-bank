package br.ufc.poo.banco.domain.model.conta;

import br.ufc.poo.banco.domain.model.cliente.Cliente;
import br.ufc.poo.banco.domain.model.cliente.PessoaFisica;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Subclasse ContaCorrente que herda de Conta.
 * Voltada para clientes Pessoa Física (PF).
 */
@Entity
@DiscriminatorValue("CORRENTE")
public class ContaCorrente extends Conta {

    public static final BigDecimal LIMITE_SAQUE_PADRAO = new BigDecimal("2000.00");
    public static final BigDecimal TARIFA_MANUTENCAO_MENSAL = new BigDecimal("15.00");

    protected ContaCorrente() {
        super();
    }

    public ContaCorrente(String numeroConta, Cliente titular, Agencia agencia) {
        super(numeroConta, titular, agencia);
    }

    @Override
    public BigDecimal obterLimiteSaqueDiario() {
        return LIMITE_SAQUE_PADRAO;
    }

    @Override
    public BigDecimal obterTarifaManutencao() {
        return TARIFA_MANUTENCAO_MENSAL;
    }

    @Override
    public boolean podeTerTitular(Cliente cliente) {
        return cliente instanceof PessoaFisica;
    }

    @Override
    public String obterTipoConta() {
        return "CORRENTE";
    }
}
