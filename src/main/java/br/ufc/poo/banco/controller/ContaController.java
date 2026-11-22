package br.ufc.poo.banco.controller;

import br.ufc.poo.banco.dto.ContaDtos.AbrirContaRequest;
import br.ufc.poo.banco.dto.ContaDtos.CadastrarChavePixRequest;
import br.ufc.poo.banco.dto.ContaDtos.ChavePixResponse;
import br.ufc.poo.banco.dto.ContaDtos.ContaResponse;
import br.ufc.poo.banco.dto.TransacaoDtos.TransacaoResponse;
import br.ufc.poo.banco.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@Tag(name = "2. Contas e PIX", description = "Gerenciamento de Contas Correntes/Empresariais, Chaves PIX e Extratos")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas bancárias e seus saldos")
    public List<ContaResponse> listarTodas() {
        return contaService.listarTodas().stream()
                .map(ContaResponse::deEntidade)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar dados e saldo de uma conta por ID")
    public ContaResponse buscarPorId(@PathVariable Long id) {
        return ContaResponse.deEntidade(contaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Abrir uma nova Conta Corrente (PF) ou Conta Empresarial (PJ)")
    public ResponseEntity<ContaResponse> abrirConta(@Valid @RequestBody AbrirContaRequest request) {
        ContaResponse response = ContaResponse.deEntidade(contaService.abrirConta(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/chaves-pix")
    @Operation(summary = "Listar chaves PIX vinculadas à conta")
    public List<ChavePixResponse> listarChavesPix(@PathVariable Long id) {
        return contaService.listarChavesPixDaConta(id).stream()
                .map(ChavePixResponse::deEntidade)
                .toList();
    }

    @PostMapping("/{id}/chaves-pix")
    @Operation(summary = "Cadastrar nova chave PIX para a conta")
    public ResponseEntity<ChavePixResponse> cadastrarChavePix(
            @PathVariable Long id,
            @Valid @RequestBody CadastrarChavePixRequest request
    ) {
        ChavePixResponse response = ChavePixResponse.deEntidade(contaService.cadastrarChavePix(id, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/extrato")
    @Operation(summary = "Consultar histórico de transações (extrato JSON) da conta")
    public List<TransacaoResponse> consultarExtrato(@PathVariable Long id) {
        return contaService.consultarHistoricoTransacoes(id).stream()
                .map(TransacaoResponse::deEntidade)
                .toList();
    }

    @GetMapping(value = "/{id}/extrato/exportar", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Exportar extrato formatado via polimorfismo (formatos: TEXTO ou CSV)")
    public String exportarExtrato(
            @PathVariable Long id,
            @RequestParam(defaultValue = "TEXTO") String formato
    ) {
        return contaService.exportarExtrato(id, formato);
    }
}
