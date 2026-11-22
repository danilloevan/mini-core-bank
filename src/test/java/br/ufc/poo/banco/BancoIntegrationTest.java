package br.ufc.poo.banco;

import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.transacao.Transacao;
import br.ufc.poo.banco.dto.TransacaoDtos.DepositoRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.PixRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.SaqueRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.TedRequest;
import br.ufc.poo.banco.service.ContaService;
import br.ufc.poo.banco.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BancoIntegrationTest {

    @Autowired
    private ContaService contaService;

    @Autowired
    private TransacaoService transacaoService;

    @Test
    @DisplayName("Deve carregar os dados iniciais da migration V2 e executar operações financeiras completas")
    void deveCarregarSeedEExecutarOperacoes() {
        // Verifica as 3 contas criadas pela migration V2__popular_dados_iniciais.sql
        List<Conta> contasIniciais = contaService.listarTodas();
        assertTrue(contasIniciais.size() >= 3);

        Conta contaMaria = contaService.buscarPorId(1L); // Saldo inicial: 3500.00 (PF)
        Conta contaJoao = contaService.buscarPorId(2L);  // Saldo inicial: 1200.00 (PF)
        Conta contaTech = contaService.buscarPorId(3L);  // Saldo inicial: 25000.00 (PJ)

        assertEquals(new BigDecimal("3500.00"), contaMaria.getSaldo());
        assertEquals(new BigDecimal("1200.00"), contaJoao.getSaldo());
        assertEquals(new BigDecimal("25000.00"), contaTech.getSaldo());

        // 1. Depósito de R$ 500,00 na conta da Maria -> Saldo vai para R$ 4.000,00
        Transacao dep = transacaoService.realizarDeposito(
                new DepositoRequest(1L, new BigDecimal("500.00"), "APP_MOBILE", "Depósito de teste")
        );
        assertNotNull(dep.getId());
        assertEquals(new BigDecimal("4000.00"), contaService.buscarPorId(1L).getSaldo());

        // 2. Saque de R$ 300,00 na conta da Maria -> Saldo vai para R$ 3.700,00
        transacaoService.realizarSaque(
                new SaqueRequest(1L, new BigDecimal("300.00"), "ATM-PICI", "Saque em dinheiro")
        );
        assertEquals(new BigDecimal("3700.00"), contaService.buscarPorId(1L).getSaldo());

        // 3. PIX de R$ 1.000,00 da empresa Tech Ceará (PJ - Conta 3) para a chave PIX do João (joao.souza@email.com)
        // Como a conta origem é PJ, aplica TarifaPercentual de 1% (R$ 10,00)
        Transacao pixPj = transacaoService.realizarPix(
                new PixRequest(3L, "joao.souza@email.com", new BigDecimal("1000.00"), "Pagamento fornecedor")
        );
        assertEquals(new BigDecimal("10.00"), pixPj.getTarifa());
        assertEquals(new BigDecimal("23990.00"), contaService.buscarPorId(3L).getSaldo()); // 25000 - 1000 - 10
        assertEquals(new BigDecimal("2200.00"), contaService.buscarPorId(2L).getSaldo());  // 1200 + 1000

        // 4. TED de R$ 200,00 do João (Conta 2) para Maria (Conta 1) -> Tarifa fixa de R$ 10,00
        Transacao ted = transacaoService.realizarTed(
                new TedRequest(2L, 1L, new BigDecimal("200.00"), "123", "Transferência TED")
        );
        assertEquals(new BigDecimal("10.00"), ted.getTarifa());
        assertEquals(new BigDecimal("1990.00"), contaService.buscarPorId(2L).getSaldo()); // 2200 - 200 - 10
        assertEquals(new BigDecimal("3900.00"), contaService.buscarPorId(1L).getSaldo()); // 3700 + 200

        // 5. Verifica o extrato exportado em formato TEXTO
        String extratoTexto = contaService.exportarExtrato(1L, "TEXTO");
        assertFalse(extratoTexto.isBlank());
        assertTrue(extratoTexto.contains("Maria Silva"));
    }
}
