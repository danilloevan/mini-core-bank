package br.ufc.poo.banco.domain.export;

import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.transacao.Transacao;

import java.util.List;

/**
 * Interface ExportadorExtrato (Requisito 2.d: Interfaces e Polimorfismo).
 * Permite exportar o extrato de uma conta em diferentes formatos (Texto ou CSV).
 */
public interface ExportadorExtrato {

    String exportar(Conta conta, List<Transacao> transacoes);
}
