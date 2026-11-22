package br.ufc.poo.banco.service;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.export.ExportadorExtrato;
import br.ufc.poo.banco.domain.model.cliente.Cliente;
import br.ufc.poo.banco.domain.model.conta.Agencia;
import br.ufc.poo.banco.domain.model.conta.ChavePix;
import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.conta.ContaCorrente;
import br.ufc.poo.banco.domain.model.conta.ContaEmpresarial;
import br.ufc.poo.banco.domain.model.transacao.Transacao;
import br.ufc.poo.banco.dto.ContaDtos.AbrirContaRequest;
import br.ufc.poo.banco.dto.ContaDtos.CadastrarChavePixRequest;
import br.ufc.poo.banco.repository.AgenciaRepository;
import br.ufc.poo.banco.repository.ChavePixRepository;
import br.ufc.poo.banco.repository.ClienteRepository;
import br.ufc.poo.banco.repository.ContaRepository;
import br.ufc.poo.banco.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final AgenciaRepository agenciaRepository;
    private final ChavePixRepository chavePixRepository;
    private final TransacaoRepository transacaoRepository;
    private final ExportadorExtrato exportadorTexto;
    private final ExportadorExtrato exportadorCsv;

    public ContaService(
            ContaRepository contaRepository,
            ClienteRepository clienteRepository,
            AgenciaRepository agenciaRepository,
            ChavePixRepository chavePixRepository,
            TransacaoRepository transacaoRepository,
            @Qualifier("exportadorTexto") ExportadorExtrato exportadorTexto,
            @Qualifier("exportadorCsv") ExportadorExtrato exportadorCsv
    ) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.agenciaRepository = agenciaRepository;
        this.chavePixRepository = chavePixRepository;
        this.transacaoRepository = transacaoRepository;
        this.exportadorTexto = exportadorTexto;
        this.exportadorCsv = exportadorCsv;
    }

    @Transactional(readOnly = true)
    public List<Conta> listarTodas() {
        return contaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Agencia> listarAgencias() {
        return agenciaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Conta não encontrada com o ID: " + id));
    }

    @Transactional
    public Conta abrirConta(AbrirContaRequest request) {
        Cliente titular = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new NegocioException("Cliente titular não encontrado com o ID: " + request.clienteId()));

        Agencia agencia = agenciaRepository.findById(request.agenciaId())
                .orElseThrow(() -> new NegocioException("Agência não encontrada com o ID: " + request.agenciaId()));

        long proximoSequencial = contaRepository.count() + 1;
        String numeroGerado = Conta.gerarNumeroContaFormatado(proximoSequencial);

        Conta novaConta;
        if ("CORRENTE".equalsIgnoreCase(request.tipoConta())) {
            novaConta = new ContaCorrente(numeroGerado, titular, agencia);
        } else if ("EMPRESARIAL".equalsIgnoreCase(request.tipoConta())) {
            novaConta = new ContaEmpresarial(numeroGerado, titular, agencia);
        } else {
            throw new NegocioException("Tipo de conta inválido. Utilize CORRENTE ou EMPRESARIAL.");
        }

        return contaRepository.save(novaConta);
    }

    @Transactional
    public ChavePix cadastrarChavePix(Long contaId, CadastrarChavePixRequest request) {
        Conta conta = buscarPorId(contaId);
        if (chavePixRepository.existsByValor(request.valor().trim())) {
            throw new NegocioException("A chave PIX '" + request.valor() + "' já está cadastrada no sistema.");
        }
        ChavePix chavePix = new ChavePix(request.tipo(), request.valor(), conta);
        return chavePixRepository.save(chavePix);
    }

    @Transactional(readOnly = true)
    public List<ChavePix> listarChavesPixDaConta(Long contaId) {
        buscarPorId(contaId);
        return chavePixRepository.findByContaId(contaId);
    }

    @Transactional(readOnly = true)
    public List<Transacao> consultarHistoricoTransacoes(Long contaId) {
        buscarPorId(contaId);
        return transacaoRepository.buscarExtratoPorContaId(contaId);
    }

    /**
     * Demonstra polimorfismo e dependência com a interface ExportadorExtrato.
     */
    @Transactional(readOnly = true)
    public String exportarExtrato(Long contaId, String formato) {
        Conta conta = buscarPorId(contaId);
        List<Transacao> transacoes = transacaoRepository.buscarExtratoPorContaId(contaId);

        ExportadorExtrato exportadorEscolhido = "CSV".equalsIgnoreCase(formato)
                ? exportadorCsv
                : exportadorTexto;

        return exportadorEscolhido.exportar(conta, transacoes);
    }
}
