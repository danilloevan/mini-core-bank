package br.ufc.poo.banco.domain;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.exception.SaldoInsuficienteException;
import br.ufc.poo.banco.domain.export.ExportadorExtrato;
import br.ufc.poo.banco.domain.export.ExportadorExtratoCsv;
import br.ufc.poo.banco.domain.export.ExportadorExtratoTexto;
import br.ufc.poo.banco.domain.model.cliente.Cliente;
import br.ufc.poo.banco.domain.model.cliente.Endereco;
import br.ufc.poo.banco.domain.model.cliente.PessoaFisica;
import br.ufc.poo.banco.domain.model.cliente.PessoaJuridica;
import br.ufc.poo.banco.domain.model.conta.Agencia;
import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.conta.ContaCorrente;
import br.ufc.poo.banco.domain.model.conta.ContaEmpresarial;
import br.ufc.poo.banco.domain.model.transacao.Transacao;
import br.ufc.poo.banco.domain.model.transacao.TransacaoDeposito;
import br.ufc.poo.banco.domain.model.transacao.TransacaoPix;
import br.ufc.poo.banco.domain.model.transacao.TransacaoSaque;
import br.ufc.poo.banco.domain.model.transacao.TransacaoTed;
import br.ufc.poo.banco.domain.strategy.CalculadoraTarifa;
import br.ufc.poo.banco.domain.strategy.TarifaFixa;
import br.ufc.poo.banco.domain.strategy.TarifaIsenta;
import br.ufc.poo.banco.domain.strategy.TarifaPercentual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DominioPooTest {

    @Test
    @DisplayName("1. Deve validar herança, composição e polimorfismo de Cliente (PessoaFisica e PessoaJuridica)")
    void deveValidarHerancaEPolimorfismoDeCliente() {
        Endereco endereco = new Endereco("Av. Humberto Monte, 2977", "Fortaleza", "CE", "60440-593");

        Cliente pf = new PessoaFisica("Maria Silva", "123.456.789-09", "maria@ufc.br", LocalDate.of(1999, 5, 10), endereco);
        Cliente pj = new PessoaJuridica("Tech Ceará", "11.222.333/0001-81", "contato@tech.com", "Tech Ceará LTDA", endereco);

        assertTrue(pf.validarDocumento());
        assertTrue(pf.isIsentoTarifaPix());
        assertEquals("PF", pf.obterTipoCliente());

        assertTrue(pj.validarDocumento());
        assertFalse(pj.isIsentoTarifaPix());
        assertEquals("PJ", pj.obterTipoCliente());

        // Valida métodos estáticos (Requisito 2.g)
        assertFalse(PessoaFisica.validarCpf("11111111111"));
        assertFalse(PessoaJuridica.validarCnpj("123"));
    }

    @Test
    @DisplayName("2. Deve validar herança, agregação e regras polimórficas de ContaCorrente e ContaEmpresarial")
    void deveValidarHerancaEPolimorfismoDeConta() {
        Agencia agencia = new Agencia("0001", "Agência Pici", "Fortaleza");
        Endereco endereco = new Endereco("Rua A, 100", "Fortaleza", "CE", "60000-000");

        Cliente pf = new PessoaFisica("João", "98765432100", "joao@ufc.br", LocalDate.of(1995, 1, 15), endereco);
        Cliente pj = new PessoaJuridica("Padaria Ideal", "11222333000181", "ideal@padaria.com", "Padaria Ideal LTDA", endereco);

        Conta contaCorrente = new ContaCorrente(Conta.gerarNumeroContaFormatado(1), pf, agencia);
        Conta contaEmpresarial = new ContaEmpresarial(Conta.gerarNumeroContaFormatado(2), pj, agencia);

        assertEquals(new BigDecimal("2000.00"), contaCorrente.obterLimiteSaqueDiario());
        assertEquals(new BigDecimal("10000.00"), contaEmpresarial.obterLimiteSaqueDiario());
        assertEquals(2, agencia.getContas().size());

        // ContaCorrente não pode ter titular PJ
        assertThrows(NegocioException.class, () -> new ContaCorrente("00099-1", pj, agencia));
    }

    @Test
    @DisplayName("3. Deve executar polimorficamente Depósito, Saque, PIX (PF/PJ) e TED com CalculadoraTarifa")
    void deveExecutarTransacoesPolimorficamente() {
        Agencia agencia = new Agencia("0001", "Agência Pici", "Fortaleza");
        Endereco endereco = new Endereco("Rua B, 200", "Fortaleza", "CE", "60000-000");

        Cliente pf = new PessoaFisica("Maria", "12345678909", "maria@ufc.br", LocalDate.of(1998, 4, 20), endereco);
        Cliente pj = new PessoaJuridica("Tech Ceará", "11222333000181", "tech@ceara.com", "Tech Ceará LTDA", endereco);

        Conta contaPf = new ContaCorrente("00001-7", pf, agencia);
        Conta contaPj = new ContaEmpresarial("00002-4", pj, agencia);

        CalculadoraTarifa isenta = new TarifaIsenta();
        CalculadoraTarifa percentualPj = new TarifaPercentual(new BigDecimal("0.01"), new BigDecimal("50.00"));
        CalculadoraTarifa fixaTed = new TarifaFixa(TransacaoTed.TARIFA_FIXA_PADRAO);

        // 1. Depósito inicial em ambas as contas
        Transacao depPf = new TransacaoDeposito(contaPf, new BigDecimal("1000.00"), "CAIXA", "Depósito PF");
        depPf.executar(isenta);

        Transacao depPj = new TransacaoDeposito(contaPj, new BigDecimal("2000.00"), "CAIXA", "Depósito PJ");
        depPj.executar(isenta);

        assertEquals(new BigDecimal("1000.00"), contaPf.getSaldo());
        assertEquals(new BigDecimal("2000.00"), contaPj.getSaldo());
        assertNotNull(depPf.getComprovante());

        // 2. PIX de PJ para PF (cobra 1% de R$ 500,00 = R$ 5,00 de tarifa)
        Transacao pixPjParaPf = new TransacaoPix(contaPj, contaPf, new BigDecimal("500.00"), "12345678909", "Pagamento serviço");
        pixPjParaPf.executar(percentualPj);

        assertEquals(new BigDecimal("1495.00"), contaPj.getSaldo()); // 2000 - 500 - 5
        assertEquals(new BigDecimal("1500.00"), contaPf.getSaldo()); // 1000 + 500

        // 3. TED de PF para PJ (cobra R$ 10,00 de tarifa fixa)
        Transacao tedPfParaPj = new TransacaoTed(contaPf, contaPj, new BigDecimal("200.00"), "123", "Devolução parcial");
        tedPfParaPj.executar(fixaTed);

        assertEquals(new BigDecimal("1290.00"), contaPf.getSaldo()); // 1500 - 200 - 10
        assertEquals(new BigDecimal("1695.00"), contaPj.getSaldo()); // 1495 + 200

        // 4. Saque acima do saldo disponível deve lançar SaldoInsuficienteException
        Transacao saqueExcessivo = new TransacaoSaque(contaPf, new BigDecimal("1500.00"), "ATM-01", "Tentativa sem saldo");
        assertThrows(SaldoInsuficienteException.class, () -> saqueExcessivo.executar(isenta));

        // 5. Exportação polimórfica de extrato (Texto e CSV)
        ExportadorExtrato exportadorTexto = new ExportadorExtratoTexto();
        ExportadorExtrato exportadorCsv = new ExportadorExtratoCsv();
        List<Transacao> historico = List.of(depPf, pixPjParaPf, tedPfParaPj);

        assertTrue(exportadorTexto.exportar(contaPf, historico).contains("SALDO ATUAL DISPONÍVEL: R$ 1290.00"));
        assertTrue(exportadorCsv.exportar(contaPf, historico).contains("DATA_HORA;TIPO_TRANSACAO"));
    }
}
