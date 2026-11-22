package br.ufc.poo.banco.dto;

import br.ufc.poo.banco.domain.model.transacao.Transacao;
import br.ufc.poo.banco.domain.model.transacao.TransacaoPix;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoDtos {

    public record DepositoRequest(
            @NotNull(message = "O ID da conta de destino é obrigatório") Long contaDestinoId,
            @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser positivo") BigDecimal valor,
            String canalDeposito,
            String descricao
    ) {}

    public record SaqueRequest(
            @NotNull(message = "O ID da conta de origem é obrigatório") Long contaOrigemId,
            @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser positivo") BigDecimal valor,
            String terminalAtm,
            String descricao
    ) {}

    public record PixRequest(
            @NotNull(message = "O ID da conta de origem é obrigatório") Long contaOrigemId,
            @NotBlank(message = "A chave PIX de destino é obrigatória") String chavePixDestino,
            @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser positivo") BigDecimal valor,
            String descricao
    ) {}

    public record TedRequest(
            @NotNull(message = "O ID da conta de origem é obrigatório") Long contaOrigemId,
            @NotNull(message = "O ID da conta de destino é obrigatório") Long contaDestinoId,
            @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser positivo") BigDecimal valor,
            String codigoBancoDestino,
            String descricao
    ) {}

    public record TransacaoResponse(
            Long id,
            String tipoTransacao,
            BigDecimal valor,
            BigDecimal tarifa,
            LocalDateTime dataHora,
            String descricao,
            String contaOrigemNumero,
            String contaDestinoNumero,
            String endToEndId,
            String codigoAutenticacao,
            String resumoComprovante
    ) {
        public static TransacaoResponse deEntidade(Transacao t) {
            String origem = t.getContaOrigem() != null ? t.getContaOrigem().getNumeroConta() : null;
            String destino = t.getContaDestino() != null ? t.getContaDestino().getNumeroConta() : null;
            String e2e = (t instanceof TransacaoPix pix) ? pix.getEndToEndId() : null;
            String auth = t.getComprovante() != null ? t.getComprovante().getCodigoAutenticacao() : null;
            String resumo = t.getComprovante() != null ? t.getComprovante().getResumoOperacao() : null;

            return new TransacaoResponse(
                    t.getId(),
                    t.obterTipoTransacao(),
                    t.getValor(),
                    t.getTarifa(),
                    t.getDataHora(),
                    t.getDescricao(),
                    origem,
                    destino,
                    e2e,
                    auth,
                    resumo
            );
        }
    }
}
