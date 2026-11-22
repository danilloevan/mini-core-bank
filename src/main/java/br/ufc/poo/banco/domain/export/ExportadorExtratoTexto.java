package br.ufc.poo.banco.domain.export;

import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.transacao.Transacao;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementação polimórfica de ExportadorExtrato para formato Texto Bancário formatado.
 */
@Component("exportadorTexto")
public class ExportadorExtratoTexto implements ExportadorExtrato {

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public String exportar(Conta conta, List<Transacao> transacoes) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================================\n");
        sb.append("           MINI CORE BANK - EXTRATO BANCÁRIO              \n");
        sb.append("==========================================================\n");
        sb.append(String.format("Banco: %s | Agência: %s (%s)\n",
                Conta.CODIGO_BANCO, conta.getAgencia().getCodigo(), conta.getAgencia().getNome()));
        sb.append(String.format("Conta: %s (%s)\n", conta.getNumeroConta(), conta.obterTipoConta()));
        sb.append(String.format("Titular: %s (%s: %s)\n",
                conta.getTitular().getNome(),
                conta.getTitular().obterTipoCliente(),
                conta.getTitular().getDocumento()));
        sb.append("----------------------------------------------------------\n");
        sb.append("DATA/HORA        | TIPO     | VALOR (R$) | TARIFA | AUTENTICAÇÃO\n");
        sb.append("----------------------------------------------------------\n");

        if (transacoes.isEmpty()) {
            sb.append("Nenhuma movimentação registrada para esta conta.\n");
        } else {
            for (Transacao t : transacoes) {
                boolean isSaida = t.getContaOrigem() != null
                        && t.getContaOrigem().getNumeroConta().equals(conta.getNumeroConta());
                String sinal = isSaida ? "-" : "+";
                String auth = t.getComprovante() != null ? t.getComprovante().getCodigoAutenticacao() : "N/A";
                sb.append(String.format("%s | %-8s | %s%-9s | %-6s | %s\n",
                        t.getDataHora().format(FORMATADOR_DATA),
                        t.obterTipoTransacao(),
                        sinal,
                        t.getValor(),
                        t.getTarifa(),
                        auth));
            }
        }
        sb.append("----------------------------------------------------------\n");
        sb.append(String.format("SALDO ATUAL DISPONÍVEL: R$ %s\n", conta.getSaldo()));
        sb.append("==========================================================");
        return sb.toString();
    }
}
