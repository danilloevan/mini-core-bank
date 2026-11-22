package br.ufc.poo.banco.domain.model.conta;

import br.ufc.poo.banco.domain.model.cliente.Cliente;
import br.ufc.poo.banco.domain.model.cliente.PessoaJuridica;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Subclasse ContaEmpresarial que herda de Conta.
 * Voltada para clientes Pessoa Jurídica (PJ), com limite de saque superior.
 */
@Entity
@DiscriminatorValue("EMPRESARIAL")
public class ContaEmpresarial extends Conta {

    public static final BigDecimal LIMITE_SAQUE_EMPRESARIAL = new BigDecimal("10000.00");
    public static final BigDecimal TARIFA_MANUTENCAO_EMPRESARIAL = new BigDecimal("45.00");

    protected ContaEmpresarial() {
        super();
    }

    public ContaEmpresarial(String numeroConta, Cliente titular, Agencia agencia) {
        super(numeroConta, titular, agencia);
    }

    @Override
    public BigDecimal obterLimiteSaqueDiario() {
        return LIMITE_SAQUE_EMPRESARIAL;
    }

    @Override
    public BigDecimal obterTarifaManutencao() {
        return TARIFA_MANUTENCAO_EMPRESARIAL;
    }

    @Override
    public boolean podeTerTitular(Cliente cliente) {
        return cliente instanceof PessoaJuridica;
    }

    @Override
    public String obterTipoConta() {
        return "EMPRESARIAL";
    }
}
