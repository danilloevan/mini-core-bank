package br.ufc.poo.banco.domain.export;

import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.transacao.Transacao;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementação polimórfica de ExportadorExtrato para formato CSV (planilhas).
 */
@Component("exportadorCsv")
public class ExportadorExtratoCsv implements ExportadorExtrato {

    private static final DateTimeFormatter FORMATADOR_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String exportar(Conta conta, List<Transacao> transacoes) {
        StringBuilder sb = new StringBuilder();
        sb.append("DATA_HORA;TIPO_TRANSACAO;NATUREZA;VALOR;TARIFA;CODIGO_AUTENTICACAO;DESCRICAO\n");
        for (Transacao t : transacoes) {
            boolean isSaida = t.getContaOrigem() != null
                    && t.getContaOrigem().getNumeroConta().equals(conta.getNumeroConta());
            String natureza = isSaida ? "DEBITO" : "CREDITO";
            String auth = t.getComprovante() != null ? t.getComprovante().getCodigoAutenticacao() : "";
            String desc = t.getDescricao() != null ? t.getDescricao() : "";
            sb.append(String.format("%s;%s;%s;%s;%s;%s;%s\n",
                    t.getDataHora().format(FORMATADOR_ISO),
                    t.obterTipoTransacao(),
                    natureza,
                    t.getValor(),
                    t.getTarifa(),
                    auth,
                    desc));
        }
        return sb.toString();
    }
}
