package br.ufc.poo.banco.domain.model.transacao;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.conta.Conta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Subclasse TransacaoTed que herda de Transacao.
 * Possui tarifa fixa padrão (atributo estático) e transfere valores entre contas.
 */
@Entity
@DiscriminatorValue("TED")
public class TransacaoTed extends Transacao {

    // Atributo estático (Requisito 2.g)
    public static final BigDecimal TARIFA_FIXA_PADRAO = new BigDecimal("10.00");

    @Column(name = "codigo_banco_destino", length = 10)
    private String codigoBancoDestino;

    protected TransacaoTed() {
        super();
    }

    public TransacaoTed(Conta contaOrigem, Conta contaDestino, BigDecimal valor, String codigoBancoDestino, String descricao) {
        super(valor, descricao, contaOrigem, contaDestino);
        this.codigoBancoDestino = (codigoBancoDestino == null || codigoBancoDestino.isBlank())
                ? Conta.CODIGO_BANCO
                : codigoBancoDestino;
    }

    @Override
    protected void validarRegrasEspecificas() {
        if (this.contaOrigem == null || this.contaDestino == null) {
            throw new NegocioException("Conta de origem e conta de destino são obrigatórias na transferência TED.");
        }
        if (this.contaOrigem.getNumeroConta().equals(this.contaDestino.getNumeroConta())) {
            throw new NegocioException("Não é permitido realizar TED para a mesma conta de origem.");
        }
    }

    @Override
    protected void efetivarMovimentacao() {
        BigDecimal totalDebitoOrigem = this.valor.add(this.tarifa);
        this.contaOrigem.debitar(totalDebitoOrigem);
        this.contaDestino.creditar(this.valor);
    }

    @Override
    public String obterTipoTransacao() {
        return "TED";
    }

    public String getCodigoBancoDestino() {
        return codigoBancoDestino;
    }
}
