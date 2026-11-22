package br.ufc.poo.banco.domain.model.transacao;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.conta.Conta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Subclasse TransacaoSaque que herda de Transacao.
 * Valida polimorficamente o limite de saque diário da conta e debita o valor + tarifa.
 */
@Entity
@DiscriminatorValue("SAQUE")
public class TransacaoSaque extends Transacao {

    @Column(name = "terminal_atm", length = 60)
    private String terminalAtm;

    protected TransacaoSaque() {
        super();
    }

    public TransacaoSaque(Conta contaOrigem, BigDecimal valor, String terminalAtm, String descricao) {
        super(valor, descricao, contaOrigem, null);
        this.terminalAtm = (terminalAtm == null || terminalAtm.isBlank()) ? "ATM-CENTRAL-01" : terminalAtm;
    }

    @Override
    protected void validarRegrasEspecificas() {
        if (this.contaOrigem == null) {
            throw new NegocioException("A conta de origem é obrigatória para realizar um saque.");
        }
        // Chamada polimórfica: ContaCorrente retorna R$ 2.000,00; ContaEmpresarial retorna R$ 10.000,00
        BigDecimal limiteConta = this.contaOrigem.obterLimiteSaqueDiario();
        if (this.valor.compareTo(limiteConta) > 0) {
            throw new NegocioException(String.format(
                    "O valor do saque (R$ %s) excede o limite permitido para o tipo de conta %s (Limite: R$ %s).",
                    this.valor, this.contaOrigem.obterTipoConta(), limiteConta
            ));
        }
    }

    @Override
    protected void efetivarMovimentacao() {
        BigDecimal totalDebito = this.valor.add(this.tarifa);
        this.contaOrigem.debitar(totalDebito);
    }

    @Override
    public String obterTipoTransacao() {
        return "SAQUE";
    }

    public String getTerminalAtm() {
        return terminalAtm;
    }
}
