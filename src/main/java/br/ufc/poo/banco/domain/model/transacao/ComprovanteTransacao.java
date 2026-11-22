package br.ufc.poo.banco.domain.model.transacao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe que representa o Comprovante de uma Transação.
 * Relação UML: COMPOSIÇÃO com Transacao (o comprovante nasce junto com a execução da transação
 * e não existe separadamente dela).
 */
@Embeddable
public class ComprovanteTransacao {

    @Column(name = "codigo_autenticacao", nullable = false, length = 60)
    private String codigoAutenticacao;

    @Column(name = "resumo_operacao", nullable = false, length = 255)
    private String resumoOperacao;

    @Column(name = "comprovante_emitido_em", nullable = false)
    private LocalDateTime emitidoEm;

    protected ComprovanteTransacao() {
        // Construtor padrão para o JPA
    }

    public ComprovanteTransacao(String tipoOperacao, BigDecimal valor, BigDecimal tarifa) {
        this.codigoAutenticacao = "AUTH-" + tipoOperacao + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.resumoOperacao = String.format("%s de R$ %s (Tarifa: R$ %s)", tipoOperacao, valor, tarifa);
        this.emitidoEm = LocalDateTime.now();
    }

    public String getCodigoAutenticacao() {
        return codigoAutenticacao;
    }

    public String getResumoOperacao() {
        return resumoOperacao;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }
}
