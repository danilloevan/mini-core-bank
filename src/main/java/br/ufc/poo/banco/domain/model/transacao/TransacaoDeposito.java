package br.ufc.poo.banco.domain.model.transacao;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.conta.Conta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Subclasse TransacaoDeposito que herda de Transacao.
 * Credita o valor diretamente na conta de destino.
 */
@Entity
@DiscriminatorValue("DEPOSITO")
public class TransacaoDeposito extends Transacao {

    @Column(name = "canal_deposito", length = 60)
    private String canalDeposito;

    protected TransacaoDeposito() {
        super();
    }

    public TransacaoDeposito(Conta contaDestino, BigDecimal valor, String canalDeposito, String descricao) {
        super(valor, descricao, null, contaDestino);
        this.canalDeposito = (canalDeposito == null || canalDeposito.isBlank()) ? "AGENCIA_CAIXA" : canalDeposito;
    }

    @Override
    protected void validarRegrasEspecificas() {
        if (this.contaDestino == null) {
            throw new NegocioException("A conta de destino é obrigatória para realizar um depósito.");
        }
    }

    @Override
    protected void efetivarMovimentacao() {
        this.contaDestino.creditar(this.valor);
    }

    @Override
    public String obterTipoTransacao() {
        return "DEPOSITO";
    }

    public String getCanalDeposito() {
        return canalDeposito;
    }
}
