package br.ufc.poo.banco.domain.model.transacao;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.conta.Conta;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Subclasse TransacaoPix que herda de Transacao.
 * Possui atributo e método static (Requisito 2.g) e transfere entre contaOrigem e contaDestino.
 */
@Entity
@DiscriminatorValue("PIX")
public class TransacaoPix extends Transacao {

    // Atributo estático (Requisito 2.g)
    public static final BigDecimal LIMITE_NOTURNO_PIX = new BigDecimal("1000.00");

    @Column(name = "chave_pix_destino", length = 120)
    private String chavePixDestino;

    @Column(name = "end_to_end_id", length = 60)
    private String endToEndId;

    protected TransacaoPix() {
        super();
    }

    public TransacaoPix(Conta contaOrigem, Conta contaDestino, BigDecimal valor, String chavePixDestino, String descricao) {
        super(valor, descricao, contaOrigem, contaDestino);
        this.chavePixDestino = chavePixDestino;
        this.endToEndId = gerarEndToEndId();
    }

    /**
     * Método estático utilitário para geração do identificador End-To-End padrão BACEN (Requisito 2.g).
     */
    public static String gerarEndToEndId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String sufixo = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "E" + Conta.CODIGO_BANCO + timestamp + sufixo;
    }

    @Override
    protected void validarRegrasEspecificas() {
        if (this.contaOrigem == null || this.contaDestino == null) {
            throw new NegocioException("Conta de origem e conta de destino são obrigatórias no PIX.");
        }
        if (this.contaOrigem.getNumeroConta().equals(this.contaDestino.getNumeroConta())) {
            throw new NegocioException("Não é permitido realizar transferência PIX para a própria conta.");
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
        return "PIX";
    }

    public String getChavePixDestino() {
        return chavePixDestino;
    }

    public String getEndToEndId() {
        return endToEndId;
    }
}
