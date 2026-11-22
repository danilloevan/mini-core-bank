package br.ufc.poo.banco.service;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.conta.ChavePix;
import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.transacao.Transacao;
import br.ufc.poo.banco.domain.model.transacao.TransacaoDeposito;
import br.ufc.poo.banco.domain.model.transacao.TransacaoPix;
import br.ufc.poo.banco.domain.model.transacao.TransacaoSaque;
import br.ufc.poo.banco.domain.model.transacao.TransacaoTed;
import br.ufc.poo.banco.domain.strategy.CalculadoraTarifa;
import br.ufc.poo.banco.domain.strategy.TarifaFixa;
import br.ufc.poo.banco.domain.strategy.TarifaIsenta;
import br.ufc.poo.banco.domain.strategy.TarifaPercentual;
import br.ufc.poo.banco.dto.TransacaoDtos.DepositoRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.PixRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.SaqueRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.TedRequest;
import br.ufc.poo.banco.repository.ChavePixRepository;
import br.ufc.poo.banco.repository.ContaRepository;
import br.ufc.poo.banco.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoService {

    private static final BigDecimal ALIQUOTA_PIX_PJ = new BigDecimal("0.01"); // 1%
    private static final BigDecimal TETO_PIX_PJ = new BigDecimal("50.00");

    private final ContaRepository contaRepository;
    private final ChavePixRepository chavePixRepository;
    private final TransacaoRepository transacaoRepository;

    public TransacaoService(
            ContaRepository contaRepository,
            ChavePixRepository chavePixRepository,
            TransacaoRepository transacaoRepository
    ) {
        this.contaRepository = contaRepository;
        this.chavePixRepository = chavePixRepository;
        this.transacaoRepository = transacaoRepository;
    }

    @Transactional
    public Transacao realizarDeposito(DepositoRequest request) {
        Conta contaDestino = buscarConta(request.contaDestinoId());

        Transacao transacao = new TransacaoDeposito(
                contaDestino,
                request.valor(),
                request.canalDeposito(),
                request.descricao() != null ? request.descricao() : "Depósito em conta"
        );

        // Execução polimórfica com TarifaIsenta
        transacao.executar(new TarifaIsenta());

        contaRepository.save(contaDestino);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao realizarSaque(SaqueRequest request) {
        Conta contaOrigem = buscarConta(request.contaOrigemId());

        Transacao transacao = new TransacaoSaque(
                contaOrigem,
                request.valor(),
                request.terminalAtm(),
                request.descricao() != null ? request.descricao() : "Saque em terminal"
        );

        // Execução polimórfica com TarifaIsenta
        transacao.executar(new TarifaIsenta());

        contaRepository.save(contaOrigem);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao realizarPix(PixRequest request) {
        Conta contaOrigem = buscarConta(request.contaOrigemId());

        ChavePix chaveDestino = chavePixRepository.findByValor(request.chavePixDestino().trim())
                .orElseThrow(() -> new NegocioException("Chave PIX de destino não encontrada: " + request.chavePixDestino()));

        Conta contaDestino = chaveDestino.getConta();

        Transacao transacao = new TransacaoPix(
                contaOrigem,
                contaDestino,
                request.valor(),
                chaveDestino.getValor(),
                request.descricao() != null ? request.descricao() : "Transferência PIX para " + contaDestino.getTitular().getNome()
        );

        // Polimorfismo duplo:
        // 1. Consulta polimorficamente se o cliente titular (PF ou PJ) é isento de tarifa PIX
        // 2. Seleciona polimorficamente a implementação de CalculadoraTarifa (TarifaIsenta ou TarifaPercentual)
        CalculadoraTarifa calculadora = contaOrigem.getTitular().isIsentoTarifaPix()
                ? new TarifaIsenta()
                : new TarifaPercentual(ALIQUOTA_PIX_PJ, TETO_PIX_PJ);

        transacao.executar(calculadora);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao realizarTed(TedRequest request) {
        Conta contaOrigem = buscarConta(request.contaOrigemId());
        Conta contaDestino = buscarConta(request.contaDestinoId());

        Transacao transacao = new TransacaoTed(
                contaOrigem,
                contaDestino,
                request.valor(),
                request.codigoBancoDestino(),
                request.descricao() != null ? request.descricao() : "Transferência TED para " + contaDestino.getTitular().getNome()
        );

        // Execução polimórfica com TarifaFixa padrão de TED (R$ 10,00)
        CalculadoraTarifa calculadora = new TarifaFixa(TransacaoTed.TARIFA_FIXA_PADRAO);
        transacao.executar(calculadora);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);
        return transacaoRepository.save(transacao);
    }

    private Conta buscarConta(Long contaId) {
        return contaRepository.findById(contaId)
                .orElseThrow(() -> new NegocioException("Conta não encontrada com o ID: " + contaId));
    }
}
